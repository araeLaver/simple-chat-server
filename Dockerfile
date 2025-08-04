# 멀티스테이지 빌드: Maven 빌드 스테이지
FROM maven:3.9.6-openjdk-17-slim AS build

WORKDIR /app
COPY pom.xml .
COPY src ./src

# Maven 빌드 (테스트 제외)
RUN mvn clean package -DskipTests

# 런타임 스테이지
FROM openjdk:17-jre-slim

# 애플리케이션 사용자 생성 (보안)
RUN addgroup --system chatapp && adduser --system --group chatapp

WORKDIR /app

# 빌드 스테이지에서 jar 파일 복사
COPY --from=build /app/target/*.jar app.jar

# 소유권 변경
RUN chown chatapp:chatapp /app/app.jar

# Koyeb 최적화된 JVM 설정
ENV JAVA_OPTS="-server -Xms64m -Xmx256m -XX:+UseG1GC -XX:MaxGCPauseMillis=100 -XX:+UseStringDeduplication -Djava.security.egd=file:/dev/./urandom -Dspring.profiles.active=prod"

# 포트 설정 (Koyeb은 PORT 환경변수 사용)
EXPOSE ${PORT:-8080}

# 애플리케이션 사용자로 실행
USER chatapp

# 헬스체크 추가
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:${PORT:-8080}/actuator/health || exit 1

# 컨테이너 시작 명령
CMD ["sh", "-c", "java $JAVA_OPTS -Dserver.port=${PORT:-8080} -jar app.jar"]