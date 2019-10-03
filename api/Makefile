S3_BUCKET_NAME=nlg-api
STACK_NAME=nlg-api
stack=lambda-custom-runtime-graphqlApi-dev
REMOTE_API_URL=https://5cf9n85hrg.execute-api.eu-central-1.amazonaws.com/Prod
DEV_API_URL=https://9u87u8ycfk.execute-api.eu-central-1.amazonaws.com/Prod
PROFILE=tm

build-deps-image:
	cd ../ && docker build -f api/Dockerfile.deps -t registry.gitlab.com/tokenmill/nlg/accelerated-text/api:deps .

build-native-image:
	cd ../ && docker build --target builder -f api/Dockerfile -t graalvm-compiler .
	docker rm build || true
	docker create --name build graalvm-compiler
	mkdir -p target
	docker cp build:/usr/src/app/target/app target/server
	docker cp build:/usr/src/app/libsunec.so target/libsunec.so

build-lambda-zip:
	cd ../ && docker build --target archiver -f api/Dockerfile -t lambda-runtime-archiver .
	docker rm build || true
	docker create --name build lambda-runtime-archiver
	docker cp build:/usr/src/app/function.zip target/function.zip

deploy-custom-runtime-lambda: build-lambda-zip
	aws cloudformation package \
        --template-file lambda.yml \
        --s3-bucket bucket-name \
        --s3-prefix folder-name \
        --output-template-file /tmp/lambda-packaged.yml
	aws cloudformation deploy \
        --template-file /tmp/lambda-packaged.yml \
        --stack-name $(stack) \
        --capabilities CAPABILITY_IAM \
        --no-fail-on-empty-changeset
	rm function.zip

deploy-lambda-via-container:
	cd ../ && docker build \
		--build-arg AWS_DEFAULT_REGION=${NLG_AWS_DEFAULT_REGION} \
		--build-arg AWS_ACCESS_KEY_ID=${NLG_AWS_ACCESS_KEY_ID} \
		--build-arg AWS_SECRET_ACCESS_KEY=${NLG_AWS_SECRET_ACCESS_KEY} \
		--build-arg S3_BUCKET="nlg-api" \
		--build-arg S3_FOLDER="graphql-dev" \
		--build-arg STACK_NAME=$(stack) \
		--target deployer \
		-f api/Dockerfile \
		-t lambda-deployer .

invoke-function:
	aws lambda invoke --function-name graphqlApi --payload '{"text":"Hello"}' /dev/stdout

unit-tests:
	clojure -A:test -e:integration

integration-tests:
	docker-compose -p test -f docker-compose.test.yml down && \
	docker-compose -p test -f docker-compose.test.yml pull && \
	docker-compose -p test -f docker-compose.test.yml build && \
	docker-compose -p test -f docker-compose.test.yml up --remove-orphans --abort-on-container-exit --exit-code-from api

lint-code:
	clojure -A:clj-kondo

build-rest-api-uberjar:
	clj -A:rest-uberjar

build-rest-api-image:
	cd ../ && docker build -f api/Dockerfile.rest-api -t registry.gitlab.com/tokenmill/nlg/accelerated-text/rest-api:latest .

upload-sam:
	aws cloudformation package --template-file sam.yaml --output-template-file output-sam.yaml --s3-bucket ${S3_BUCKET_NAME} --profile ${PROFILE}

deploy: build-legacy-uberjar upload-sam
	aws cloudformation deploy --template-file output-sam.yaml --stack-name ${STACK_NAME} --capabilities CAPABILITY_IAM --profile ${PROFILE}

upload-sam-dev:
	aws cloudformation package --template-file sam.yaml --output-template-file output-sam.yaml --s3-bucket ${S3_BUCKET_NAME}-dev --profile ${PROFILE}

deploy-dev: build-legacy-uberjar upload-sam-dev
	aws cloudformation deploy --template-file output-sam.yaml --stack-name ${STACK_NAME}-dev --capabilities CAPABILITY_IAM --profile ${PROFILE}

install-aws-sam:
	pip install --user aws-sam-cli awscli

test-generate-text:
	curl -X POST "${REMOTE_API_URL}/nlg" -H "Content-Type: application/json" -d '{"documentPlanId": "8fa07eda-68d1-480f-a8e5-d39385977ca3", "dataId": "-1"}'

test-generate-text-dev:
	curl -X POST "${DEV_API_URL}/nlg" -H "Content-Type: application/json" -d '{"documentPlanId": "8fa07eda-68d1-480f-a8e5-d39385977ca3", "dataId": "example-user/data-example.csv"}'

list-log-groups:
	aws logs describe-log-groups --query "*" --profile=${PROFILE}

run-local:
	clj -A:rest-uberjar && java -jar target/api-1.0.0-SNAPSHOT-standalone.jar
