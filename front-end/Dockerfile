FROM node:11-slim

RUN apt-get update -qq && apt-get install -y -qq build-essential rsync
# RUN apt-get install -y python3-pip
# RUN pip3 install awscli --upgrade

ARG ACC_TEXT_API_URL
ARG ACC_TEXT_GRAPHQL_URL
ARG ACC_TEXT_DATA_FILES_BUCKET

ENV ACC_TEXT_API_URL=$ACC_TEXT_API_URL
ENV ACC_TEXT_GRAPHQL_URL=$ACC_TEXT_GRAPHQL_URL
ENV DATA_FILES_BUCKET=$ACC_TEXT_DATA_FILES_BUCKET


RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app

COPY front-end/package.json /usr/src/app
COPY front-end/ /usr/src/app/
COPY api/resources/schema.graphql /usr/src/app/packages/graphql/schema.graphql

RUN make setup

CMD ["make", "run"]
