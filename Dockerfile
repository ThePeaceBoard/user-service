# Stage 1: Build the application
FROM gradle:7.5.1-jdk17 AS build

# Set working directory inside the container
WORKDIR /app

# Copy Gradle wrapper and build files
COPY gradlew .
COPY gradle ./gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .

# Download Gradle dependencies
RUN ./gradlew build --no-daemon -x test

# Copy the rest of the application source code
COPY src ./src

# Build the Kotlin Spring Boot application
RUN ./gradlew bootJar --no-daemon -x test

# Stage 2: Create the final image with the Spring Boot JAR
FROM openjdk:17-jdk-alpine

# Set working directory inside the final container
WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Expose the application port (usually 8080 for Spring Boot)
EXPOSE 3001

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
