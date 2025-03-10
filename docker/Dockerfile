FROM maven:3.9.9-eclipse-temurin-23-alpine AS exceptionStarter
WORKDIR /app
COPY /exceptionStarter/pom.xml /app/
COPY /exceptionStarter/src /app/src
RUN mvn clean install -DskipTests

FROM maven:3.9.9-eclipse-temurin-23-alpine AS loggingStarter
WORKDIR /app
COPY /loggingStarter/pom.xml /app/
COPY /loggingStarter/src /app/src
RUN mvn clean install -DskipTests

FROM maven:3.9.9-eclipse-temurin-23-alpine AS build
ARG FILE_PATH
WORKDIR /app
COPY --from=exceptionStarter /root/.m2 /root/.m2
COPY --from=loggingStarter /root/.m2 /root/.m2
COPY /${FILE_PATH}/pom.xml /app/
COPY /${FILE_PATH}/src /app/src/
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
