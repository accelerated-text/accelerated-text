BUILD_DIR=dist

.PHONY: test
test: setup
	npm test --silent

.PHONY: run
run: setup
	npm start

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
