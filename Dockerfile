# Stage 1: Build frontend assets with Node.js + pnpm
FROM node:alpine AS frontend

WORKDIR /app
COPY src/main/resources/web ./src/main/resources/web
COPY package.json pnpm-lock.yaml esbuild.config.mjs ./

# Install pnpm globally
RUN npm install -g pnpm

# Install dependencies
RUN pnpm i

# Run esbuild
RUN pnpm build

# Stage 2: Build quarkus
FROM quay.io/quarkus/ubi-quarkus-mandrel-builder-image:jdk-21 AS gradle

WORKDIR /app
COPY --from=frontend /app/src ./src
COPY . .

# Build native image
RUN ./gradlew build -Dquarkus.native.enabled=true -Dquarkus.package.jar.enabled=false

# Stage 3: Quarkus micro image
FROM quay.io/quarkus/quarkus-micro-image:2.0
ARG PROJECT=mood
ARG VERSION=0.8.0
ARG PORT=8080

WORKDIR /app

# Copy the Quarkus runner
COPY --from=gradle "/app/build/*-runner" "/app/mood"

VOLUME ["/app/jwt"]

RUN chown 1001 /app && chmod "g+rwX" /app && chown 1001:root /app

EXPOSE $PORT
USER 1001

# Run Quarkus app
ENTRYPOINT ["/bin/sh", "-c"]
CMD ["/app/mood"]
