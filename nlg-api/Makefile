S3_BUCKET_NAME=nlg-api
STACK_NAME=nlg-api
REMOTE_API_URL=https://zihxo6d93h.execute-api.eu-central-1.amazonaws.com/Prod
USER_BASE=$(shell python -m site --user-base)


build:
	lein do clean, uberjar

upload-sam:
	${USER_BASE}/bin/aws cloudformation package --template-file sam.yaml --output-template-file output-sam.yaml --s3-bucket ${S3_BUCKET_NAME} --profile tm

deploy: build upload-sam
	${USER_BASE}/bin/aws cloudformation deploy --template-file output-sam.yaml --stack-name ${STACK_NAME} --capabilities CAPABILITY_IAM --profile tm


install-aws-sam:
	pip install --user aws-sam-cli awscli

test-add-workspace:
	curl -X POST "${REMOTE_API_URL}/document-plans" -H "Content-Type: application/json" -d '{"name": "test-workspace", "blocklyXml": {}, "germlinCode": {}}'

test-generate-text:
	curl -X POST "${REMOTE_API_URL}/nlg" -H "Content-Type: application/json" -d '{"documentPlanId": "6c91d58d-6815-4269-93f7-33673be9400c", "dataId": "-1"}'

test-upload-data:
	curl -X POST "${REMOTE_API_URL}/data" -H "Content-Type: text/csv" --data-binary @resources/example-data.csv

list-log-groups:
	aws logs describe-log-groups --query "*" --profile=tm

