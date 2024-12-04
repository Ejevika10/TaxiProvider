FROM maven:3.9.9-eclipse-temurin-23-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src/
RUN mvn clean package -DskipTests

FROM openjdk:23-slim
WORKDIR /app
RUN apt-get update && apt-get install -y curl && apt-get clean
RUN curl --version
ARG SERVICE_NAME
COPY --from=build /app/target/${SERVICE_NAME}-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]