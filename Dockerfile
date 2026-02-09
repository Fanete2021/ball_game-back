FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app
COPY . .
RUN mvn clean compile dependency:copy-dependencies -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=builder /app/target/dependency/* /app/lib/
COPY --from=builder /app/target/classes /app/classes

RUN echo '#!/bin/sh' > /app/run.sh && \
    echo 'java -cp "/app/classes:/app/lib/*" com.game.Main' >> /app/run.sh && \
    chmod +x /app/run.sh

EXPOSE 8080
CMD ["/app/run.sh"]