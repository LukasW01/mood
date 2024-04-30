## Stage 1 : gradle build image with native capabilities
FROM docker.io/gradle:jdk21-graal-jammy as builder
ARG user=kotlin
ARG port=8080
ARG VERSION=0.5
ARG TARGETOS
ARG TARGETARCH

RUN adduser --disabled-password --gecos "" $user

WORKDIR /app
COPY . .

RUN gradle build -Dquarkus.package.type=native -x spotlessKotlin diktatCheck 

COPY /app/build/native/mood-runner /app/application
RUN chown -R $user /app \
    && chmod "g+rwX" /app

VOLUME ["/app/jwt"]

EXPOSE $port
USER $user
ENTRYPOINT ["./application", "-Dquarkus.http.host=0.0.0.0"]
