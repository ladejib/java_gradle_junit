FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Install bash (for entrypoint script)
RUN apk add --no-cache bash

# Copy entrypoint script
COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

# Use entrypoint for compilation and running
ENTRYPOINT ["/entrypoint.sh"]

