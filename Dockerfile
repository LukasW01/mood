## Stage 1 : gradle build image with native capabilities
FROM docker.io/gradle:jdk21-graal-jammy as builder

WORKDIR /gradle
COPY . .

RUN gradle build -Dquarkus.package.type=native -x spotlessKotlin diktatCheck

## Stage 2 : Add build artifacts to a micro base image, tuned for Quarkus native executables
FROM quay.io/quarkus/quarkus-micro-image:2.0
ARG PROJECT=mood
ARG VERSION=0.5

WORKDIR /app
COPY --from=builder "/gradle/build/$PROJECT-$VERSION-native-image-source-jar/" "/app/build/"
VOLUME ["/app/jwt"]

RUN chown -R $USER /app \
    && chmod "g+rwX" /app

EXPOSE 8080
USER 1001
ENTRYPOINT ["./application/build/$PROJECT-$VERSION-runner", "-Dquarkus.http.host=0.0.0.0"]
