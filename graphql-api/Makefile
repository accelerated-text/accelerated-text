build-native-image:
	docker build --target builder -t graalvm-compiler .
	docker rm build || true
	docker create --name build graalvm-compiler
	docker cp build:/usr/src/app/target/app server
	docker cp build:/usr/src/app/libsunec.so libsunec.so

build-lambda-zip:
	docker build --target archiver -t lambda-runtime-archiver .
	docker rm build || true
	docker create --name build lambda-runtime-archiver
	docker cp build:/usr/src/app/function.zip function.zip

stack=lambda-custom-runtime-graphqlApi-dev

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
	docker build --target deployer \
	--build-arg AWS_DEFAULT_REGION=${NLG_AWS_DEFAULT_REGION} \
	--build-arg AWS_ACCESS_KEY_ID=${NLG_AWS_ACCESS_KEY_ID} \
	--build-arg AWS_SECRET_ACCESS_KEY=${NLG_AWS_SECRET_ACCESS_KEY} \
	--build-arg S3_BUCKET="nlg-api" \
	--build-arg S3_FOLDER="graphql-dev" \
	--build-arg STACK_NAME=$(stack) \
	-t lambda-deployer .

invoke-function:
	aws lambda invoke --function-name graphqlApi --payload '{"text":"Hello"}' /dev/stdout

build-rest-api-docker:
	docker pull registry.gitlab.com/tokenmill/nlg/accelerated-text/graphql-api:deps
	docker build -f Dockerfile.rest-api -t registry.gitlab.com/tokenmill/nlg/accelerated-text/graphql-rest-api:latest .

unit-tests:
	clojure -A:test -e:integration

integration-tests:
	docker-compose -p test -f docker-compose.test.yml down && \
	docker-compose -p test -f docker-compose.test.yml build && \
	docker-compose -p test -f docker-compose.test.yml up --remove-orphans --abort-on-container-exit --exit-code-from graphql-api

run-local:
	clj -A:rest-uberjar && java -jar target/graphql-api-1.0.0-SNAPSHOT-standalone.jar
