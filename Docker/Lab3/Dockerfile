# Stage 1: Build the application using Maven
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY building_stat/Lab1 /app
RUN mvn clean package -DskipTests

# Stage 2: Run the application using Eclipse Temurin JRE
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/target/calculator.jar app.jar
CMD ["java", "-jar", "app.jar"]
