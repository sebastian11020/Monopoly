# Use an official OpenJDK runtime as a parent image
FROM eclipse-temurin:21-jdk-alpine

# Set the working directory
WORKDIR /app

# Copy Maven Wrapper and application files to the container
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
COPY src ./src

# Build the application
RUN ./mvnw clean package -DskipTests

# Expose the port the app runs on
EXPOSE 8080

# Run the application using Maven Wrapper
CMD ["./mvnw", "spring-boot:run"]
