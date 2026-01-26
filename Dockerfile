# Stage 0: Build frontend assets with Node.js + pnpm
FROM node:alpine AS frontend

WORKDIR /app
COPY src/main/resources/web/app ./src/main/resources/web/app
COPY package.json pnpm-lock.yaml esbuild.config.mjs ./

# Install pnpm globally
RUN corepack enable && corepack prepare pnpm@latest --activate

# Install dependencies
RUN pnpm install --frozen-lockfile

# Run esbuild
RUN node esbuild.config.mjs

# Stage 1: Quarkus micro image
FROM quay.io/quarkus/quarkus-micro-image:2.0
ARG PROJECT=mood
ARG VERSION=0.7.1
ARG PORT=8080

WORKDIR /app

# Copy the Quarkus runner
COPY "build/*-runner" "/app/mood"
# Copy built frontend assets
COPY --from=frontend /app/src/main/resources/META-INF/resources/static /app/META-INF/resources/static

VOLUME ["/app/jwt"]

RUN chown 1001 /app && chmod "g+rwX" /app && chown 1001:root /app

EXPOSE $PORT
USER 1001

ENTRYPOINT ["/bin/sh", "-c"]
CMD ["/app/mood"]
