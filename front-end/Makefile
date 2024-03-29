BUILD_DIR=dist

FRONTEND_IMAGE=acctext/frontend:latest

.PHONY: test
test: setup
	npm test --silent

.PHONY: run
run: setup
	npm start

run-w-proxy: setup
	(nginx -g "daemon on;" && npm start)

.PHONY: npm-audit
npm-audit:
	npm audit

.PHONY:	build
build: setup
	npm run build
	rsync -aL assets/ "${BUILD_DIR}/"

.PHONY: clean
clean:
	rm -rf node_modules "${BUILD_DIR}"

.PHONY: setup
setup: node_modules

node_modules: package.json
	npm install
	touch -m node_modules

prepare-builder:
	docker build -t frontend-builder --build-arg DATA_FILES_BUCKET="http://localhost:3001/accelerated-text-data-files"  -f Dockerfile.builder ../

copy-resources:
	docker run -v "${PWD}/dist:/opt/output" -t frontend-builder cp -r /var/www/acc-text/ /opt/output

compile-frontend-services: prepare-builder copy-resources

build-docker:
	docker build -t ${FRONTEND_IMAGE} -f Dockerfile ../


publish-frontend-docker: build-docker
	docker push ${FRONTEND_IMAGE}

integration-tests:
	docker-compose -p test -f docker-compose.test.yml down && \
	docker-compose -p test -f docker-compose.test.yml build && \
	docker-compose -p test -f docker-compose.test.yml up --remove-orphans --abort-on-container-exit --exit-code-from tests
