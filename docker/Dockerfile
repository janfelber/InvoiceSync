# Builder image
FROM eclipse-temurin:23-jre AS builder
WORKDIR /builder
ARG JAR_FILE=InvoiceSync-be/target/invoicesync-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} application.jar

# Runtime image
FROM eclipse-temurin:23-jre
WORKDIR /application
COPY --from=builder /builder/application.jar .

# Run the jar directly
ENTRYPOINT ["java", "-jar", "application.jar"]