FROM openjdk:17-jre-slim

WORKDIR /app
COPY target/*.jar app.jar

# 극도로 최적화된 JVM 설정
ENV JAVA_OPTS="-Xms32m -Xmx128m -XX:+UseSerialGC -XX:MaxMetaspaceSize=64m -Djava.security.egd=file:/dev/./urandom -XX:TieredStopAtLevel=1"

EXPOSE 8000

CMD ["java", "-jar", "app.jar"]