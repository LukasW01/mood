## Stage 1 : gradle build image with native capabilities
FROM docker.io/gradle:jdk21-graal-jammy as builder
ARG USER=kotlin
ARG PORT=8080
ARG PROJECT=mood
ARG VERSION=0.5
ARG TARGETOS
ARG TARGETARCH

WORKDIR /gradle
COPY . .

RUN gradle build -Dquarkus.package.type=native -x spotlessKotlin diktatCheck

## Stage 2 : Add build artifacts to a micro base image, tuned for Quarkus native executables
FROM quay.io/quarkus/quarkus-micro-image:2.0

RUN adduser --disabled-password --gecos "" $user

WORKDIR /app
COPY --copy=builder "/gradle/build/$PROJECT-$VERSION-native-image-source-jar/$PROJECT-$VERSION-runner" /app/application
VOLUME ["/app/jwt"]

COPY /app/build/native/mood-runner /app/application
RUN chown -R $USER /app \
    && chmod "g+rwX" /app

EXPOSE $PORT
USER $USER
ENTRYPOINT ["./application", "-Dquarkus.http.host=0.0.0.0"]
