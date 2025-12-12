# Builder image
FROM eclipse-temurin:23-jre AS builder
WORKDIR /builder
COPY target/invoicesync-0.0.1-SNAPSHOT.jar application.jar

# Runtime image
FROM eclipse-temurin:23-jre
WORKDIR /application
COPY --from=builder /builder/application.jar .

EXPOSE 8080

# Run the jar directly
ENTRYPOINT ["java", "-jar", "application.jar"]