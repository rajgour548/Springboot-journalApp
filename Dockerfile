
# Stage 1: Build the Spring Boot application
FROM maven:3.9.9-eclipse-temurin-8 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:8-jre

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 10000

ENTRYPOINT ["sh", "-c", "java -XX:+UseSerialGC -Xss512k -Xms64m -Xmx220m -XX:MaxMetaspaceSize=96m -Dserver.port=${PORT:-10000} -jar app.jar"]
