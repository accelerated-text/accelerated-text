#!/bin/bash

set -e

# Prepare document plan for eval

echo "Waiting for ${ACC_TEXT_URL}/health"

while [[ "$(curl --insecure -s -o /dev/null -w ''%{http_code}'' ${ACC_TEXT_URL}/health)" != "200" ]]; do sleep 1; done

echo "Uploading data to: ${ACC_TEXT_URL}"
curl -XPOST ${ACC_TEXT_URL}/_graphql -H 'Content-Type: application/json' -d @data/bleu-plan.json

exec "$@"
