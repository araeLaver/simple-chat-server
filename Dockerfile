FROM openjdk:17-jre-slim

# 필요한 패키지 설치
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# 작업 디렉토리 설정
WORKDIR /app

# JAR 파일 복사
COPY target/*.jar app.jar

# 메모리 최적화를 위한 JVM 옵션
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC -XX:MaxGCPauseMillis=200"

# 포트 노출
EXPOSE 8000

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:${PORT:-8000}/actuator/health || exit 1

# 애플리케이션 실행
CMD ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]