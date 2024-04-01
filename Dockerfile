FROM gradle:alpine as builder
ARG user=gradle
ARG port=8080
ARG VERSION=0.5

RUN adduser --disabled-password --gecos "" $user

WORKDIR /app
COPY . .
RUN gradle buildNative
RUN chown -R $user:$user /app

USER $user
EXPOSE $port
ENTRYPOINT ["/bin/sh", "-c"]
CMD ["./build/mood-kotlin-$VERSION-runner"]

