#!/usr/bin/env bash

(export AWS_ACCESS_KEY_ID=dev && \
    export AWS_SECRET_ACCESS_KEY=dev && \
    export AWS_DEFAULT_REGION=eu-west-1 && \
    aws \
    --endpoint-url=http://localhost:4569 \
    dynamodb create-table \
    --table-name data-files \
    --attribute-definitions AttributeName=id,AttributeType=S \
    --key-schema AttributeName=id,KeyType=HASH \
    --provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5)
