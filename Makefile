FRONT_END_MAKE= cd front-end && make
PROJECT_NAME=accelerated-text

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

test-gf-service:
	docker build -t gf-test -f core/gf/Dockerfile.test . && docker run -it gf-test

run-dev-env:
	docker-compose -p dev -f docker-compose.yml -f docker-compose.front-end.yml down && \
	docker-compose -p dev -f docker-compose.yml -f docker-compose.front-end.yml build && \
	docker-compose -p dev -f docker-compose.yml -f docker-compose.front-end.yml up --remove-orphans

run-dev-api:
	docker-compose -p dev -f docker-compose.yml down && \
	docker-compose -p dev -f docker-compose.yml build && \
	docker-compose -p dev -f docker-compose.yml up --remove-orphans

run-dev-api-with-mocks:
	docker-compose -p dev -f docker-compose.yml -f docker-compose.mocks.yml down && \
	docker-compose -p dev -f docker-compose.yml -f docker-compose.mocks.yml build && \
	docker-compose -p dev -f docker-compose.yml -f docker-compose.mocks.yml up --remove-orphans

run-front-end-dev:
	ACC_TEXT_API_URL=http://0.0.0.0:3001 \
	ACC_TEXT_GRAPHQL_URL=http://0.0.0.0:3001/_graphql \
	MOCK_SHOP_API_URL=http://0:0:0:0:8090 \
	cd front-end && make run
