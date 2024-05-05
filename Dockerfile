## Stage 1 : gradle build image with native capabilities
FROM docker.io/gradle:jdk21-graal-jammy as builder

WORKDIR /gradle
COPY . .

RUN gradle build -Dquarkus.package.type=native -x spotlessKotlin diktatCheck 

## Stage 2 : Add build artifacts to a micro base image, tuned for Quarkus native executables
FROM quay.io/quarkus/quarkus-micro-image:2.0
ARG PROJECT=mood
ARG VERSION=0.5
ARG PORT=8080

WORKDIR /app
COPY --from=builder "/gradle/build/" "/app/build/"
VOLUME ["/app/jwt"]

RUN chown 1001 /app \
    && chmod "g+rwX" /app \
    && chown 1001:root /app

EXPOSE $PORT
USER 1001

ENTRYPOINT ["/bin/sh", "-c"]
CMD ["/app/build/mood-0.5-runner"]
