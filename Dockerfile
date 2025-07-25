FROM openjdk:17-jre-slim

WORKDIR /app
COPY target/*.jar app.jar

# Koyeb free tier에 최적화된 JVM 설정
ENV JAVA_OPTS="-Xms64m -Xmx256m -XX:+UseSerialGC -XX:MaxMetaspaceSize=128m -Djava.security.egd=file:/dev/./urandom"

EXPOSE 8000

CMD ["sh", "-c", "java $JAVA_OPTS -Dserver.port=${PORT:-8000} -jar app.jar"]