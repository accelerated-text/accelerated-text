FROM node:11-slim as builder

RUN apt-get update -qq && apt-get install -y -qq build-essential rsync
# chromium libatk-bridge2.0-0 libgtk-3-0

ARG ACC_TEXT_API_URL
ARG ACC_TEXT_GRAPHQL_URL
ARG DATA_FILES_BUCKET

ENV ACC_TEXT_API_URL=$ACC_TEXT_API_URL
ENV ACC_TEXT_GRAPHQL_URL=$ACC_TEXT_GRAPHQL_URL
ENV DATA_FILES_BUCKET=$DATA_FILES_BUCKET

WORKDIR /usr/src/app
COPY front-end/package.json /usr/src/app
COPY front-end/ /usr/src/app/
COPY api/resources/schema.graphql /usr/src/app/packages/graphql/schema.graphql

RUN make build

RUN mkdir -p /opt/dist

RUN cp -r dist/* /opt/dist/

FROM nginx:latest

RUN mkdir -p /var/www/acc-text/

COPY --from=builder /opt/dist /var/www/acc-text

COPY front-end/nginx.conf /etc/nginx/nginx.conf
