# Build stage
FROM gradle:jdk21-jammy AS build
WORKDIR /home/gradle/src
COPY . .
RUN gradle build -x test

# Package stage
FROM eclipse-temurin:21
WORKDIR /app
COPY --from=build /home/gradle/src/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
