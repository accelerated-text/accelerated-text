S3_BUCKET_NAME=nlg-api
STACK_NAME=nlg-api
REMOTE_API_URL=https://5cf9n85hrg.execute-api.eu-central-1.amazonaws.com/Prod
DEV_API_URL=https://9u87u8ycfk.execute-api.eu-central-1.amazonaws.com/Prod
PROFILE=tm

unit-tests:
	clj -A:test -e :integration

integration-tests:
	docker-compose -p test -f docker-compose.test.yml down && \
	docker-compose -p test -f docker-compose.test.yml build && \
	docker-compose -p test -f docker-compose.test.yml up --remove-orphans --abort-on-container-exit --exit-code-from legacy-api

build:
	clj -A:rest-uberjar

upload-sam:
	aws cloudformation package --template-file sam.yaml --output-template-file output-sam.yaml --s3-bucket ${S3_BUCKET_NAME} --profile ${PROFILE}

deploy: build upload-sam
	aws cloudformation deploy --template-file output-sam.yaml --stack-name ${STACK_NAME} --capabilities CAPABILITY_IAM --profile ${PROFILE}

upload-sam-dev:
	aws cloudformation package --template-file sam.yaml --output-template-file output-sam.yaml --s3-bucket ${S3_BUCKET_NAME}-dev --profile ${PROFILE}

deploy-dev: build upload-sam-dev
	aws cloudformation deploy --template-file output-sam.yaml --stack-name ${STACK_NAME}-dev --capabilities CAPABILITY_IAM --profile ${PROFILE}


install-aws-sam:
	pip install --user aws-sam-cli awscli

test-add-workspace:
	curl -X POST "${REMOTE_API_URL}/document-plans" -H "Content-Type: application/json" -d '{"name": "test-workspace", "blocklyXml": {}, "germlinCode": {}}'

test-generate-text:
	curl -X POST "${REMOTE_API_URL}/nlg" -H "Content-Type: application/json" -d '{"documentPlanId": "8fa07eda-68d1-480f-a8e5-d39385977ca3", "dataId": "-1"}'

test-generate-text-dev:
	curl -X POST "${DEV_API_URL}/nlg" -H "Content-Type: application/json" -d '{"documentPlanId": "8fa07eda-68d1-480f-a8e5-d39385977ca3", "dataId": "example-user/data-example.csv"}'

test-generate-text-dev-ccg:
	curl -X POST "${DEV_API_URL}/nlg" -H "Content-Type: application/json" -d '{"documentPlanId": "8fa07eda-68d1-480f-a8e5-d39385977ca3", "dataId": "example-user/ccg-example.csv", "ccg": true}'

list-log-groups:
	aws logs describe-log-groups --query "*" --profile=${PROFILE}

deploy-grammar:
	aws s3 sync grammar/ s3://ccg-grammar/grammar --profile=${PROFILE}

build-legacy-rest-api-image:
	docker build -f Dockerfile.legacy-rest-api \
	--build-arg AWS_DEFAULT_REGION=${NLG_AWS_DEFAULT_REGION} \
	--build-arg AWS_ACCESS_KEY_ID=${NLG_AWS_ACCESS_KEY_ID} \
	--build-arg AWS_SECRET_ACCESS_KEY=${NLG_AWS_SECRET_ACCESS_KEY} \
	-t registry.gitlab.com/tokenmill/nlg/accelerated-text/nlg-rest-api:latest .
