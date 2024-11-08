# Stage 1: Build the application
FROM maven:3.8.4-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean install

# Stage 2: Run the application
FROM openjdk:17-alpine
WORKDIR /app

# Install necessary libraries
RUN apk update && apk add --no-cache freetype


COPY --from=build /app/target/EIMS-FUHCM-BE-0.0.1-SNAPSHOT.jar ./eims-aws.jar
EXPOSE 8080
CMD ["java", "-Djava.awt.headless=true", "--enable-preview", "-jar", "eims-aws.jar"]