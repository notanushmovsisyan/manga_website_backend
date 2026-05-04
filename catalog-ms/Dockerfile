FROM openjdk:26-ea-slim

WORKDIR /app

# Copy the JAR file to created folder
COPY target/catalog-ms-0.0.1-SNAPSHOT.jar .

ENTRYPOINT ["java", "-jar", "/app/catalog-ms-0.0.1-SNAPSHOT.jar"]