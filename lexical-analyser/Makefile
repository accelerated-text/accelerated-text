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
