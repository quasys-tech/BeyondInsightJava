FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/BeyondInsight-1.0-SNAPSHOT.jar /app/BeyondInsight.jar
COPY lib/json-20240303.jar /app/lib/json-20240303.jar


RUN jar tf /app/BeyondInsight.jar

ENTRYPOINT ["java", "-cp", "/app/BeyondInsight.jar:/app/lib/json-20240303.jar", "com.quasys.Main"]
