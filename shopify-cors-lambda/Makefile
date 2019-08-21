LOCAL_PORT ?=	8090
ZIP_FILE =		lambda.zip

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
	API_URL="http://localhost:${LOCAL_PORT}/mock-shop" node server.js --mock-shop


.PHONY: clean
clean:
	rm -f "${ZIP_FILE}"


.PHONY: setup
setup: node_modules


.PHONY: zip
zip: ${ZIP_FILE}


.PHONY: publish
publish: ${ZIP_FILE}
	aws lambda update-function-code \
		--function-name "${LAMBDA_ARN}" \
		--zip-file "fileb://${ZIP_FILE}" \
		--publish


### Targets --------------------------------------------------------------------

node_modules: package.json
	npm install
	touch -m node_modules

${ZIP_FILE}: index.js
	zip "${ZIP_FILE}" index.js
