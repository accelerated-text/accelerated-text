FROM node:11-slim as builder

RUN apt-get update -qq && apt-get install -y -qq build-essential chromium libatk-bridge2.0-0 libgtk-3-0 rsync

ARG ACC_TEXT_API_URL
ARG ACC_TEXT_GRAPHQL_URL
ARG ACC_TEXT_DATA_FILES_BUCKET

ENV ACC_TEXT_API_URL=$ACC_TEXT_API_URL
ENV ACC_TEXT_GRAPHQL_URL=$ACC_TEXT_GRAPHQL_URL
ENV DATA_FILES_BUCKET=$ACC_TEXT_DATA_FILES_BUCKET

WORKDIR /usr/src/app
COPY front-end/package.json /usr/src/app

RUN make build

FROM nginx:latest

RUN mkdir -p /var/www/acc-text/

COPY --from=builder /opt/dist /var/www/acc-text

COPY front-end/nginx.conf /etc/nginx/nginx.conf
