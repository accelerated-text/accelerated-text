FROM python:3.6-slim

RUN pip3 install pipenv

WORKDIR /opt/app

COPY Pipfile Pipfile
COPY Pipfile.lock Pipfile.lock
COPY src src
COPY data data

RUN pipenv install

RUN pipenv run python -m spacy download en

CMD ["pipenv", "run", "python", "-m", "src.server"]
