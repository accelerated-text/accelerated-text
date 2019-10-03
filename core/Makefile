TEST_IMAGE=registry.gitlab.com/tokenmill/nlg/ccg-kit/test

__PHONY__:

docker-login:
	docker login registry.gitlab.com

docker-build-test:
	docker build -f Dockerfile.test -t ${TEST_IMAGE} .

docker-push-test: docker-login docker-build-test
	docker push ${TEST_IMAGE}

unit-tests:
	clj -Atest

lint:
	clj -Aeastwood
	clj -Akibit

package:
	clj -Auberjar
