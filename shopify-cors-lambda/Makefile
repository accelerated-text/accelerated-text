LOCAL_PORT ?=		8090
CORS_ZIP_FILE =		cors-lambda.zip
MOCK_ZIP_FILE =		mock-lambda.zip

### Setup ----------------------------------------------------------------------

-include .env
export

### Tasks ----------------------------------------------------------------------

.PHONY: default
default:
	Usage: make task


.PHONY: run
run: setup
	node server.js

.PHONY: run-local
run-local: setup
	API_URL="http://localhost:${LOCAL_PORT}/mock-shop" \
		node server.js


.PHONY: clean
clean:
	rm -f \
		"${CORS_ZIP_FILE}" \
		"${MOCK_ZIP_FILE}"


.PHONY: setup
setup: node_modules


.PHONY: zip
zip: ${CORS_ZIP_FILE} ${MOCK_ZIP_FILE}


.PHONY: publish
publish: publish-cors-lambda

.PHONY: publish-cors-lambda
publish-cors-lambda: ${CORS_ZIP_FILE}
	aws lambda update-function-code \
		--function-name "${CORS_LAMBDA_ARN}" \
		--zip-file "fileb://${CORS_ZIP_FILE}" \
		--publish

.PHONY: publish-mock-lambda
publish-mock-lambda: ${MOCK_ZIP_FILE}
	aws lambda update-function-code \
		--function-name "${MOCK_LAMBDA_ARN}" \
		--zip-file "fileb://${MOCK_ZIP_FILE}" \
		--publish


### Targets --------------------------------------------------------------------

node_modules: package.json
	npm install
	touch -m node_modules

${CORS_ZIP_FILE}: cors-lambda/index.js
	cd cors-lambda && \
		zip "../${CORS_ZIP_FILE}" index.js

${MOCK_ZIP_FILE}: mock-shop-lambda/index.js
	cd mock-shop-lambda && \
		zip "../${MOCK_ZIP_FILE}" index.js
