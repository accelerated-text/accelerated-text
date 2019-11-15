FROM clojure:tools-deps-alpine

RUN mkdir /root/.gitlibs

WORKDIR /usr/src/app
COPY ./core/deps.edn /usr/src/core/

RUN clojure -R:test || true

COPY ./core /usr/src/app