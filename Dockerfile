# Use base image
FROM openjdk:21-jdk-slim

# Ensure system is updated and OpenSSL is patched
RUN apt-get update && \
    apt-get upgrade -y && \
    apt-get install -y --only-upgrade openssl libssl3 && \
    rm -rf /var/lib/apt/lists/*

# Create group and user (Debian style)
RUN groupadd -r fusion && useradd -r -g fusion fusion

# Working directory inside container
WORKDIR /app

# Copy JAR file (wildcard match)
COPY ./target/api-gateway-0.0.1-SNAPSHOT.jar /app/api-gateway.jar

# Change ownership of app files
RUN chown -R fusion:fusion /app

# Switch to non-root user
USER fusion

# Expose application port
EXPOSE 8080

# Run JAR file
CMD ["java", "-jar", "/app/api-gateway.jar"]