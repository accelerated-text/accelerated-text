BUILD_DIR=dist
S3_BUCKET=accelerated-text-app
S3_BUCKET_URL=s3://${S3_BUCKET}

.PHONY: test
test: setup
	npm test --silent

.PHONY: run
run: setup
	npm start

.PHONY: deploy
deploy: build
	aws s3 sync "${BUILD_DIR}/" "${S3_BUCKET_URL}" \
		--acl public-read \
		--delete

.PHONY: fix-s3-permissions
fix-s3-permissions:
	aws s3 ls --recursive "${S3_BUCKET_URL}" \
		| awk '{ print $$4 }' \
		| xargs -n 1 aws s3api put-object-acl --acl public-read --bucket ${S3_BUCKET} --key

.PHONY: npm-audit
npm-audit:
	npm audit

.PHONY:	build
build: setup
	npm run build
	rsync -aL assets/ "${BUILD_DIR}/"

.PHONY: clean
clean:
	rm -rf node_modules package-lock.json "${BUILD_DIR}"

.PHONY: setup
setup: node_modules


node_modules: package.json
	npm install
	touch -m node_modules

build-perform-api-docker:
	docker build -f Dockerfile -t registry.gitlab.com/tokenmill/nlg/accelerated-text/perform-api:latest .
