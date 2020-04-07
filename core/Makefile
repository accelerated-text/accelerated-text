lint:
	clojure -Sdeps '{:deps {clj-kondo {:mvn/version "RELEASE"}}}' -m clj-kondo.main --lint src test

unit-tests:
	clojure -A:test -e:integration

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

test-gf-service:
	cd gf && docker build -t gf .
	docker run gf pytest test.py
