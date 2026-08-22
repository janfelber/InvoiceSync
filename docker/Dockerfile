# Stage 1: Build the JAR from source using Maven
FROM eclipse-temurin:23-jdk AS builder
WORKDIR /builder
COPY InvoiceSync-be/pom.xml .
COPY InvoiceSync-be/.mvn .mvn
COPY InvoiceSync-be/mvnw .
RUN ./mvnw dependency:go-offline
COPY InvoiceSync-be/src ./src
RUN ./mvnw clean package -DskipTests

# Stage 2: Runtime image
FROM eclipse-temurin:23-jre
WORKDIR /application
COPY --from=builder /builder/target/*.jar application.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "application.jar"]