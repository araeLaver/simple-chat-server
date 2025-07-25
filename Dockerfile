FROM openjdk:17-jre-slim

WORKDIR /app
COPY target/*.jar app.jar

# 메모리 제한에 맞는 JVM 설정
ENV JAVA_OPTS="-Xms128m -Xmx400m -XX:+UseSerialGC"

EXPOSE 8000

CMD ["sh", "-c", "java $JAVA_OPTS -Dserver.port=${PORT:-8000} -jar app.jar"]