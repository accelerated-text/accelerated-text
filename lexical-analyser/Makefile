download-spacy-models:
	pipenv run python -m spacy download en

run: download-spacy-models
	pipenv run gunicorn api.wsgi --reload

deploy:
	rsync -r .  --exclude '*/__pycache__/' --exclude '*.pyc' root@word2vec.tokenmill.lt:/opt/lexical-analyser/
