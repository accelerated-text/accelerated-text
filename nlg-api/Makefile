S3_BUCKET_NAME=nlg-api
STACK_NAME=nlg-api
REMOTE_API_URL="https://zihxo6d93h.execute-api.eu-central-1.amazonaws.com/Prod/document-plans"
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
	curl -X POST ${REMOTE_API_URL} -H "Content-Type: application/json" -d '{"name": "test-workspace", "blocklyXml": {}, "germlinCode": {}}'
