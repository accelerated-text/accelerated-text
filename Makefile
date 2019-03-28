PREACT_MAKE= cd preact-demo && make
PROJECT_NAME=accelerated-text
PYTEST_DOCKER="registry.gitlab.com/tokenmill/nlg/${PROJECT_NAME}/pytest:latest"

.PHONY: test
test:
	${PREACT_MAKE} test

.PHONY: run
run:
	${PREACT_MAKE} run

.PHONY: build-app
build-app:
	${PREACT_MAKE} build

.PHONY: deploy-app
deploy-app:
	${PREACT_MAKE} deploy

.PHONY: clean
clean:
	${PREACT_MAKE} clean

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
