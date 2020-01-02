FROM python:3

WORKDIR /usr/src/app

RUN pip install --no-cache-dir requests future

COPY . .

ENTRYPOINT [ "/bin/sh", "entrypoint.sh" ]

CMD [ "python", "./eval.py" ]
