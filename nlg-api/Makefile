S3_BUCKET_NAME=nlg-api
STACK_NAME=nlg-api
REMOTE_API_URL=https://5cf9n85hrg.execute-api.eu-central-1.amazonaws.com/Prod
DEV_API_URL=https://9u87u8ycfk.execute-api.eu-central-1.amazonaws.com/Prod


build:
	lein do clean, uberjar

upload-sam:
	aws cloudformation package --template-file sam.yaml --output-template-file output-sam.yaml --s3-bucket ${S3_BUCKET_NAME} --profile tm

deploy: build upload-sam
	aws cloudformation deploy --template-file output-sam.yaml --stack-name ${STACK_NAME} --capabilities CAPABILITY_IAM --profile tm

upload-sam-dev:
	aws cloudformation package --template-file sam.yaml --output-template-file output-sam.yaml --s3-bucket ${S3_BUCKET_NAME}-dev --profile tm

deploy-dev: build upload-sam-dev
	aws cloudformation deploy --template-file output-sam.yaml --stack-name ${STACK_NAME}-dev --capabilities CAPABILITY_IAM --profile tm


install-aws-sam:
	pip install --user aws-sam-cli awscli

test-add-workspace:
	curl -X POST "${REMOTE_API_URL}/document-plans" -H "Content-Type: application/json" -d '{"name": "test-workspace", "blocklyXml": {}, "germlinCode": {}}'

test-generate-text:
	curl -X POST "${REMOTE_API_URL}/nlg" -H "Content-Type: application/json" -d '{"documentPlanId": "8fa07eda-68d1-480f-a8e5-d39385977ca3", "dataId": "-1"}'

test-generate-text-dev:
	curl -X POST "${DEV_API_URL}/nlg" -H "Content-Type: application/json" -d '{"documentPlanId": "8fa07eda-68d1-480f-a8e5-d39385977ca3", "dataId": "example-user/data-example.csv"}'

list-log-groups:
	aws logs describe-log-groups --query "*" --profile=tm

run-local-server:
	lein with-profile local-server run
