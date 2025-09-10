# Build stage
FROM gradle:jdk21-jammy AS build
WORKDIR /home/gradle/src
COPY . .
RUN gradle build -x test

# Package stage
FROM openjdk:21-jre-slim
WORKDIR /app
COPY --from=build /home/gradle/src/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
