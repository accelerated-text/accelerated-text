#!/bin/bash

set -e

# Prepare document plan for eval

echo "Waiting for ${ACC_TEXT_URL}/health"

while [[ "$(curl --insecure -s -o /dev/null -w ''%{http_code}'' ${ACC_TEXT_URL}/health)" != "200" ]]; do sleep 1; done

echo "Uploading required AMRs"

for f in data/*.yaml; do
  filepath=$(basename -- "$f")
  filename="${filepath%.*}"
  curl -XPOST ${ACC_TEXT_URL}/amr/${filename} -H 'Content-Type: application/yaml' --data-binary @${f}
done

echo "Uploading data to: ${ACC_TEXT_URL}"


DOCUMENT_PLAN_ID=$(curl -XPOST ${ACC_TEXT_URL}/_graphql -H 'Content-Type: application/json' -d @data/bleu-plan.json | jq ".data.createDocumentPlan.id")

export DOCUMENT_PLAN_ID

echo $DOCUMENT_PLAN_ID

exec "$@"
