FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY target/content-ms-0.0.1-SNAPSHOT.jar .

ENTRYPOINT ["java", "-jar", "/app/content-ms-0.0.1-SNAPSHOT.jar"]