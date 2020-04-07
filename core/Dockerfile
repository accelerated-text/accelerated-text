FROM clojure:tools-deps-alpine

RUN mkdir /root/.gitlibs

WORKDIR /usr/src/app
COPY deps.edn ./
RUN clojure -R:test || true

COPY . ./
