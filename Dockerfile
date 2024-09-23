FROM quay.io/quarkus/quarkus-micro-image:2.0
ARG PROJECT=mood
ARG VERSION=0.5
ARG PORT=8080

WORKDIR /app
COPY "build/*-runner" "/app/build/"
VOLUME ["/app/jwt"]

RUN chown 1001 /app && chmod "g+rwX" /app && chown 1001:root /app

EXPOSE $PORT
USER 1001

ENTRYPOINT ["/bin/sh", "-c"]
CMD ["/app/build/mood-0.5-runner"]
