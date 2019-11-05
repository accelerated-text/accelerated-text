#!/usr/bin/env bash

declare -a arr=(
  "dictionary-combined,key"
  "reader-flag,id"
  "dictionary,id"
  "amr-verbclass,id"
  "phrase,id"
  "phrase-usage-model,prim-kvs"
  "data-files,id"
  "reader-flag-usage,prim-kvs"
  "lexicon,key"
  "amr-members,id"
  "blockly-workspace,id"
  "semantic-graph-instances,id"
  "data,key"
  "nlg-results,key"
  )

for i in "${arr[@]}"
do
    IFS=',' read tableName tableKey <<< "${i}"
    echo "Creating table:" "${tableName}" "with key:" "${tableKey}"
    awslocal dynamodb create-table \
    --table-name "${tableName}" \
    --attribute-definitions AttributeName="${tableKey}",AttributeType=S \
    --key-schema AttributeName="${tableKey}",KeyType=HASH \
    --provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5 > /dev/null
done
