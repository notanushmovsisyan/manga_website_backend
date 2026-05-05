FROM openjdk:26-ea-slim

WORKDIR /app

COPY target/content-ms-0.0.1-SNAPSHOT.jar .

ENTRYPOINT ["java", "-jar", "/app/content-ms-0.0.1-SNAPSHOT.jar"]