FROM python:3

WORKDIR /usr/src/app

RUN pip install --no-cache-dir requests future gql

RUN apt-get update && apt-get install jq -y

COPY . .

ENTRYPOINT [ "/bin/bash", "entrypoint.sh" ]

CMD [ "python", "./eval.py" ]
