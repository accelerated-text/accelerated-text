FROM python:3.6-slim

RUN pip3 install pipenv

WORKDIR /opt/app

COPY core/enrich/Pipfile Pipfile
COPY core/enrich/Pipfile.lock Pipfile.lock
COPY core/enrich/src src
COPY core/enrich/data data

RUN pipenv install

RUN pipenv run python -m spacy download en

CMD ["pipenv", "run", "python", "-m", "src.server"]
