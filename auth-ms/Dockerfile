FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy the JAR file to created folder
COPY target/auth-ms-0.0.1-SNAPSHOT.jar .

ENTRYPOINT ["java", "-jar", "/app/auth-ms-0.0.1-SNAPSHOT.jar"]