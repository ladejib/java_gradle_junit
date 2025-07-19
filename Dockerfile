# Use the official Gradle image with JDK 17
FROM gradle:8.0.2-jdk17 AS builder

# Set the working directory inside the container
WORKDIR /app

# Copy the project files (excluding unnecessary files via .dockerignore)
COPY . .

# Run Gradle build (replace with your actual task, e.g., 'build', 'test', 'bootJar')
RUN gradle build --no-daemon

# (Optional) Multi-stage build to reduce final image size
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar ./app.jar
CMD ["java", "-jar", "app.jar"]
