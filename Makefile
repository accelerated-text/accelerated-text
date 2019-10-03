FRONT_END_MAKE= cd front-end && make
PROJECT_NAME=accelerated-text
PYTEST_DOCKER="registry.gitlab.com/tokenmill/nlg/${PROJECT_NAME}/pytest:latest"
DYNAMODB_ENDPOINT="http://dynamodb.eu-central-1.amazonaws.com"
DYNAMODB_LOCAL_ENDPOINT="http://localhost:8000"

-include .env
export

.PHONY: test
test:
	${FRONT_END_MAKE} test

.PHONY: run
run:
	${FRONT_END_MAKE} run

.PHONY: build-app
build-app:
	${FRONT_END_MAKE} build

.PHONY: deploy-app
deploy-app:
	${FRONT_END_MAKE} deploy

.PHONY: clean
clean:
	${FRONT_END_MAKE} clean

.PHONY: npm-audit
npm-audit:
	${FRONT_END_MAKE} npm-audit

docker-repo-login:
	docker login registry.gitlab.com

build-demo-test-env:
	(cd dockerfiles && docker build -f Dockerfile.test-env -t registry.gitlab.com/tokenmill/nlg/${PROJECT_NAME}/demo-test-env:latest .)

publish-demo-test-env: build-demo-test-env
	docker push registry.gitlab.com/tokenmill/nlg/${PROJECT_NAME}/demo-test-env:latest


build-pytest-docker:
	(cd dockerfiles && docker build -f Dockerfile.pytest -t ${PYTEST_DOCKER} .)

publish-pytest-docker: build-pytest-docker
	docker push ${PYTEST_DOCKER}

build-dynamodb-docker:
	docker pull amazon/dynamodb-local
	docker run -d -p 8000:8000 --name dynamo-build amazon/dynamodb-local -jar DynamoDBLocal.jar -sharedDb
	cd api && clj -e "(require '[data.db.dynamo-ops :refer [clone-tables-to-local-db]]) (clone-tables-to-local-db \"${DYNAMODB_ENDPOINT}\" \"${DYNAMODB_LOCAL_ENDPOINT}\" 100) (System/exit 0)"
	docker commit dynamo-build registry.gitlab.com/tokenmill/nlg/accelerated-text/dynamodb-local:latest
	docker stop dynamo-build
	docker rm dynamo-build

build-s3-docker:
	docker pull localstack/localstack:latest
	docker run -d -p 8000:4572 --name s3-build -e SERVICES=s3 -e DATA_DIR=/tmp/localstack/data localstack/localstack:latest
	sleep 15
	aws s3 mb s3://accelerated-text-data-files/ --endpoint-url http://localhost:8000
	aws s3 cp api/resources/accelerated-text-data-files s3://accelerated-text-data-files --recursive --endpoint-url http://localhost:8000
	docker commit s3-build registry.gitlab.com/tokenmill/nlg/accelerated-text/s3-local:latest
	docker stop s3-build
	docker rm s3-build

publish-s3-docker:
	docker push registry.gitlab.com/tokenmill/nlg/accelerated-text/s3-local:latest

publish-dynamodb-docker:
	docker push registry.gitlab.com/tokenmill/nlg/accelerated-text/dynamodb-local:latest

run-dev-env:
	docker-compose -p dev -f docker-compose.yml -f docker-compose.front-end.yml pull && \
	docker-compose -p dev -f docker-compose.yml -f docker-compose.front-end.yml down && \
	docker-compose -p dev -f docker-compose.yml -f docker-compose.front-end.yml build && \
	docker-compose -p dev -f docker-compose.yml -f docker-compose.front-end.yml up --remove-orphans

run-dev-env-no-api:
	docker-compose -p dev -f docker-compose.yml -f docker-compose.front-end.yml pull && \
	docker-compose -p dev -f docker-compose.yml -f docker-compose.front-end.yml down && \
	docker-compose -p dev -f docker-compose.yml -f docker-compose.front-end.yml build && \
	docker-compose -p dev -f docker-compose.yml -f docker-compose.front-end.yml up --remove-orphans s3 dynamodb mock-shop front-end

restart-api-service:
	docker-compose -p dev -f docker-compose.yml -f docker-compose.front-end.yml restart acc-text-api

.PHONY: run-front-end-dev-deps
run-front-end-dev-deps:
	docker-compose -p dev -f docker-compose.yml pull && \
	docker-compose -p dev -f docker-compose.yml down && \
	docker-compose -p dev -f docker-compose.yml build && \
	docker-compose -p dev -f docker-compose.yml up --remove-orphans

.PHONY: run-front-end-dev-deps-no-api
run-front-end-dev-deps-no-api:
	docker-compose -p dev -f docker-compose.yml pull && \
	docker-compose -p dev -f docker-compose.yml down && \
	docker-compose -p dev -f docker-compose.yml build && \
	docker-compose -p dev -f docker-compose.yml up --remove-orphans s3 dynamodb mock-shop

.PHONY: run-front-end-dev
run-front-end-dev:
	cd front-end && \
      	ACC_TEXT_API_URL=http://0.0.0.0:3001 \
      	ACC_TEXT_GRAPHQL_URL=http://0.0.0.0:3001/_graphql \
      	MOCK_SHOP_API_URL=http://0:0:0:0:8090 \
		make run

clojure-code-analysis:
	clojure -Sdeps "{:deps {jonase/kibit {:mvn/version \"0.1.6\"}}}" -e "(require '[kibit.driver :as k]) (k/external-run [\"api/src\"] nil)"
