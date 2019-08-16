ARN=arn:aws:lambda:eu-central-1:372973017485:function:shopify-fake-store-proxy
FUNCTION_NAME=${ARN}
ZIP_FILE=lambda.zip

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
		--function-name "${FUNCTION_NAME}" \
		--zip-file "fileb://${ZIP_FILE}" \
		--publish


### Targets --------------------------------------------------------------------

${ZIP_FILE}: index.js
	zip "${ZIP_FILE}" index.js
