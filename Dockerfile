## Use Maven with Eclipse Temurin JDK 17 image as the build environment
#FROM maven:3-eclipse-temurin-17 AS build
#
## Set working directory inside the build container
#WORKDIR /app
#
## Copy all project files into the working directory
#COPY . .
#
## Build the project, skipping tests to speed up the build
#RUN mvn clean package -DskipTests
#
## Use lightweight Eclipse Temurin JDK 17 Alpine image as the runtime environment
#FROM eclipse-temurin:17-alpine
#
## Set working directory inside the runtime container
#WORKDIR /app
#
## Copy the built JAR file from the build container to the runtime container
## The JAR is located in /app/target/ because of the WORKDIR in build stage
#COPY --from=build /app/target/*.jar dashboard-be-0.0.1-SNAPSHOT.jar
#
## Expose port 8080 so the container listens on this port at runtime
#EXPOSE 8080
#
## Specify the command to run the JAR file when the container starts
#ENTRYPOINT ["java", "-jar", "dashboard-be-0.0.1-SNAPSHOT.jar"]


# Use an official Maven image to build the Spring Boot app
FROM maven:3.8.4-openjdk-17 AS build

# Set working directory inside the build container
WORKDIR /app

# Copy pom.xml and install dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

#Copy the source code and built the application
# Build the project, skipping tests to speed up the build
COPY src ./src
RUN mvn clean package -DskipTests

#Use an official OpenJDK image to run the app
FROM openjdk:17-jdk-slim

# Set working directory inside the runtime container
WORKDIR /app

# Copy the built JAR file from the build container to the runtime container
# The JAR is located in /app/target/ because of the WORKDIR in build stage
COPY --from=build /app/target/dashboard-be-0.0.1-SNAPSHOT.jar .

# Expose port 8080 so the container listens on this port at runtime
EXPOSE 8080

# Specify the command to run the JAR file when the container starts
ENTRYPOINT ["java", "-jar", "dashboard-be-0.0.1-SNAPSHOT.jar"]
#ENTRYPOINT ["java", "-jar", "/app/dashboard-be-0.0.1-SNAPSHOT.jar"]


#restart docker if the nex command doesn't work
#docker build -t demo-deployment .
#docker tag demo-deployment alexnehoi/demo-deployment:latest
#docker push alexnehoi/demo-deployment:latest