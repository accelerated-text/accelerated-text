lint-code:
	clojure -A:clj-kondo

unit-tests:
	clojure -A:test -e:integration

integration-tests:
	docker-compose -p test -f docker-compose.test.yml down && \
	docker-compose -p test -f docker-compose.test.yml build && \
	docker-compose -p test -f docker-compose.test.yml up --remove-orphans --abort-on-container-exit --exit-code-from api

run-local:
	clojure -A:dev -m api.server
