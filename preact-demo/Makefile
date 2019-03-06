S3_BUCKET=augmented-writer-app
S3_BUCKET_URL=s3://${S3_BUCKET}

.PHONY: test
test: setup
	npm test

.PHONY: run
run: setup
	npm start

.PHONY: deploy
deploy: build
	rsync -aL assets/ dist/
	aws s3 sync dist/ "${S3_BUCKET_URL}" \
		--acl public-read \
		--delete

.PHONY: fix-s3-permissions
fix-s3-permissions:
	aws s3 ls --recursive "${S3_BUCKET_URL}" \
		| awk '{ print $$4 }' \
		| xargs -n 1 aws s3api put-object-acl --acl public-read --bucket ${S3_BUCKET} --key

.PHONY:	build
build: setup
	npm run build

.PHONY: clean
clean:
	rm -rf node_modules package-lock.json

.PHONY: setup
setup: node_modules


node_modules: package.json
	npm install
	touch -m node_modules
