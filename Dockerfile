# ----------- Stage 1: Build -----------
FROM maven:3.9.6-eclipse-temurin-21 AS builder

# Set working directory inside container
WORKDIR /app

# Copy pom.xml and download dependencies (cache layer)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build application
COPY src ./src
RUN mvn clean package -DskipTests

# ----------- Stage 2: Runtime -----------
FROM eclipse-temurin:21-jdk-alpine

# Set working directory
WORKDIR /app

# Create upload directories inside the container
RUN mkdir -p /app/uploads/posts
RUN mkdir -p /app/uploads/images

# Copy the packaged JAR from builder stage
COPY --from=builder /app/target/FriendBook-1-0.0.1-SNAPSHOT.jar app.jar

# Expose application port
EXPOSE 8090

# Define volume for uploads (so you can mount host directory if needed)
VOLUME ["/app/uploads"]

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
