lint:
	clojure -Sdeps '{:deps {clj-kondo {:mvn/version "RELEASE"}}}' -m clj-kondo.main --lint src test

unit-tests:
	clojure -Atest

integration-tests:
	docker-compose -p test -f docker-compose.test.yml down && \
	docker-compose -p test -f docker-compose.test.yml build && \
	docker-compose -p test -f docker-compose.test.yml up --remove-orphans --abort-on-container-exit --exit-code-from core
