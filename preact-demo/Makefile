.PHONY: test
test: setup
	npm test

.PHONY: run
run: setup
	npm start

.PHONY: clean
clean:
	rm -rf node_modules package-lock.json

.PHONY: setup
setup: node_modules


node_modules: package.json
	npm install
	touch -m node_modules
