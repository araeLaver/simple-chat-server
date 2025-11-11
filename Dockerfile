# 멀티스테이지 빌드: Maven 빌드 스테이지
FROM eclipse-temurin:17-jdk-jammy AS build

WORKDIR /app

# Maven 설치
RUN apt-get update && \
    apt-get install -y maven && \
    rm -rf /var/lib/apt/lists/*

# Maven 메모리 최적화 (Koyeb 무료 플랜 대응)
ENV MAVEN_OPTS="-Xmx512m -Xms256m"

COPY pom.xml .
COPY src ./src

# Maven 빌드 (테스트 제외)
RUN mvn clean package -DskipTests

# 런타임 스테이지
FROM eclipse-temurin:17-jre-jammy

# 애플리케이션 사용자 생성 (보안)
RUN addgroup --system chatapp && adduser --system --group chatapp

WORKDIR /app

# 빌드 스테이지에서 jar 파일 복사
COPY --from=build /app/target/*.jar app.jar

# 소유권 변경
RUN chown chatapp:chatapp /app/app.jar

# Koyeb 최적화된 JVM 설정
ENV JAVA_OPTS="-server -Xms64m -Xmx256m -XX:+UseG1GC -XX:MaxGCPauseMillis=100 -XX:+UseStringDeduplication -Djava.security.egd=file:/dev/./urandom"

# 포트 설정 (Koyeb은 PORT 환경변수 사용)
EXPOSE ${PORT:-8080}

# 애플리케이션 사용자로 실행
USER chatapp

# 컨테이너 시작 명령 (SPRING_PROFILES_ACTIVE는 환경변수로 설정)
CMD ["sh", "-c", "java $JAVA_OPTS -Dserver.port=${PORT:-8080} -jar app.jar"]