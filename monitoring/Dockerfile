# Stage 1: Build the application
FROM maven:3.8.3-openjdk-17 AS builder

# Set the working directory in the build container
WORKDIR /app

# Copy the Maven project files to the container
COPY pom.xml .
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Stage 2: Create the runtime image
FROM openjdk:17-jdk-slim

# Set the working directory in the runtime container
WORKDIR /app

# Copy the JAR file from the build container to the runtime container
COPY --from=builder /app/target/monitoring-0.0.1-SNAPSHOT.jar app.jar

# Expose the port your application will run on
EXPOSE 8083

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
