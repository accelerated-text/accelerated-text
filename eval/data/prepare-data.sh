#!/bin/sh

echo "Uploading data to: ${ACC_TEXT_URL}"
curl -XPOST ${ACC_TEXT_URL}/_graphql -H 'Content-Type: application/json' -d @data/bleu-plan.json
