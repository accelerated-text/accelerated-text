PROJECT_TAG=lexical-analyser

init:
	pip install pipenv

download-spacy-models:
	pipenv run python -m spacy download en

install-dev-deps:
	pipenv install --dev


run: download-spacy-models
	pipenv run gunicorn api.wsgi --reload

deploy:
	rsync -r .  --exclude '*/__pycache__/' --exclude '*.pyc' root@word2vec.tokenmill.lt:/opt/lexical-analyser/

tests: init install-dev-deps download-spacy-models
	pipenv run python -m pytest tests

build-docker:
	docker build -t ${PROJECT_TAG} .

run-docker: build-docker
	docker run -d -p 8000:8000 ${PROJECT_TAG}
