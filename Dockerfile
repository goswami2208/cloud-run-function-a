# Stage 1: Build the function using a Maven container
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
# This will now create a single, executable uber-jar in /app/target
RUN mvn package

# Stage 2: Create the final, lightweight runtime container
FROM openjdk:21-slim
WORKDIR /app

# The final JAR name is based on the pom.xml's artifactId and version
COPY --from=build /app/target/function-a.jar function.jar

EXPOSE 8080

CMD ["java", "-jar", "function.jar"]