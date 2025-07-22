FROM maven:3.8-openjdk-17 AS build

WORKDIR /build
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim

WORKDIR /app
COPY --from=build /build/target/learning-app-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8007 8087

ENTRYPOINT ["java", "-jar", "app.jar"]
