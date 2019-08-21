ZIP_FILE=lambda.zip

### Setup ----------------------------------------------------------------------

-include .env.defaults
-include .env
export

### Tasks ----------------------------------------------------------------------

.PHONY: default
default:
	Usage: make task


.PHONY: run
run:
	node server.js

.PHONY: clean
clean:
	rm -f "${ZIP_FILE}"


.PHONY: zip
zip: ${ZIP_FILE}


.PHONY: publish
publish: ${ZIP_FILE}
	aws lambda update-function-code \
		--function-name "${LAMBDA_ARN}" \
		--zip-file "fileb://${ZIP_FILE}" \
		--publish


### Targets --------------------------------------------------------------------

${ZIP_FILE}: index.js
	zip "${ZIP_FILE}" index.js
