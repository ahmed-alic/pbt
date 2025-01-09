FROM maven:3.9.9-eclipse-temurin-21-alpine AS build
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:23-jdk-slim
COPY --from=build /target/Personal-Budget-Tracker-0.0.1-SNAPSHOT.jar pbt.jar
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "pbt.jar"]
