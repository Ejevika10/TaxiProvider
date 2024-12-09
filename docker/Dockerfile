FROM maven:3.9.9-eclipse-temurin-23-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src/
RUN mvn clean package -DskipTests

FROM openjdk:23-slim as optimize
ARG SERVICE_NAME
WORKDIR /app
COPY --from=build /app/target/${SERVICE_NAME}-0.0.1-SNAPSHOT.jar ${SERVICE_NAME}.jar
RUN java -Djarmode=layertools -jar ${SERVICE_NAME}.jar extract

FROM openjdk:23-slim
WORKDIR /app
RUN apt-get update && apt-get install -y curl && apt-get clean
COPY --from=optimize /app/dependencies/ ./
COPY --from=optimize /app/spring-boot-loader/ ./
COPY --from=optimize /app/snapshot-dependencies/ ./
COPY --from=optimize /app/application/ ./
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
