PROJECT_NAME=accelerated-text

pull-latest-mages:
	docker pull acctext/gf
	docker pull acctext/api
	docker pull acctext/frontend

run-app:
	docker-compose -p dev -f docker-compose.yml -f docker-compose.front-end.yml down && \
	docker-compose -p dev -f docker-compose.yml -f docker-compose.front-end.yml up --remove-orphans

run-dev-env:
	docker-compose -p dev -f docker-compose.yml -f docker-compose.front-end.yml -f docker-compose.dev.yml down && \
	docker-compose -p dev -f docker-compose.yml -f docker-compose.front-end.yml -f docker-compose.dev.yml pull && \
	docker-compose -p dev -f docker-compose.yml -f docker-compose.front-end.yml -f docker-compose.dev.yml build && \
	docker-compose -p dev -f docker-compose.yml -f docker-compose.front-end.yml -f docker-compose.dev.yml up --remove-orphans

run-dev-api:
	docker-compose -p dev -f docker-compose.yml -f docker-compose.dev.yml down && \
	docker-compose -p dev -f docker-compose.yml -f docker-compose.dev.yml pull && \
	docker-compose -p dev -f docker-compose.yml -f docker-compose.dev.yml build && \
	docker-compose -p dev -f docker-compose.yml -f docker-compose.dev.yml up --remove-orphans

run-dev-no-api:
	docker-compose -p dev -f docker-compose.front-end.yml up -d --remove-orphans
	cd core/gf && docker build -t dev_gf .
	docker run --rm --net dev_default --name dev_gf -p 8001:8000 dev_gf

run-eval:
	git submodule update --init --recursive && \
	docker-compose -p dev -f docker-compose.yml -f docker-compose.eval.yml down && \
	docker-compose -p dev -f docker-compose.yml -f docker-compose.eval.yml build && \
	docker-compose -p dev -f docker-compose.yml -f docker-compose.eval.yml up --remove-orphans --abort-on-container-exit --exit-code-from eval

run-front-end-dev:
	ACC_TEXT_API_URL=http://0.0.0.0:3001 \
	ACC_TEXT_GRAPHQL_URL=http://0.0.0.0:3001/_graphql \
	cd front-end && make run

delete-datomic-volume:
	docker-compose -p dev -f docker-compose.yml -f docker-compose.front-end.yml -f docker-compose.dev.yml down && \
	docker volume rm acc-text_datomic
