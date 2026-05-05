FROM openjdk:26-ea-slim

WORKDIR /app

COPY target/tracking-ms-0.0.1-SNAPSHOT.jar .

ENTRYPOINT ["java", "-jar", "/app/tracking-ms-0.0.1-SNAPSHOT.jar"]