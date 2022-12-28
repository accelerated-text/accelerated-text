FROM clojure:tools-deps-alpine

RUN mkdir /root/.gitlibs

WORKDIR /usr/src/app
COPY ./core/deps.edn /usr/src/core/
COPY ./api/deps.edn /usr/src/app/

RUN clojure -X:deps prep

COPY ./api /usr/src/app
COPY ./core /usr/src/core

CMD ["clojure", "-m", "api.server"]
