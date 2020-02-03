FROM python:3

WORKDIR /usr/src/app

RUN pip install --no-cache-dir requests future

RUN apt-get update && apt-get install jq -y

RUN wget https://github.com/a8m/envsubst/releases/download/v1.1.0/envsubst-Linux-x86_64 -O /usr/local/bin/envsubst


COPY . .

ENTRYPOINT [ "/bin/bash", "entrypoint.sh" ]

CMD [ "python", "./eval.py" ]
