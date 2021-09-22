lint:
	clj -M:lint

fix-format:
	clojure -M:format fix

unit-tests:
	clojure -A:test -e:integration

code-coverage:
	clojure -A:coverage

integration-tests:
	docker-compose -p test -f docker-compose.test.yml down && \
	docker-compose -p test -f docker-compose.test.yml build && \
	docker-compose -p test -f docker-compose.test.yml up --remove-orphans --abort-on-container-exit --exit-code-from core

.PHONY: gf
gf:
	/usr/bin/gf -i gf/lang-utils

clean-gf-comple-files:
	find . -name "*.gfo" -exec rm -rf {} \;
	find . -name "*.pgf" -exec rm -rf {} \;

run-gf-service:
	cd gf && docker build -t dev_gf .
	docker run --rm --net dev_default --name dev_gf -p 8001:8000 dev_gf

test-gf-service:
	cd gf && docker build -t gf .
	docker run gf pytest test.py
