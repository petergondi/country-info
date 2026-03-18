# Stage 1: Build
FROM maven:3.9.5-eclipse-temurin-21 AS builder
WORKDIR /app

COPY pom.xml .
# Download dependencies - use --fail-never so missing network doesn't abort
RUN mvn dependency:go-offline -B --fail-never

COPY src ./src
# Package the application, skipping tests
RUN mvn clean package -DskipTests -B -e 2>&1

# Stage 2: Run
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S appgroup && adduser -S appuser -G appgroup
RUN mkdir -p /app/logs && chown -R appuser:appgroup /app

COPY --from=builder /app/target/country-service-1.0.0.jar app.jar

USER appuser
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]