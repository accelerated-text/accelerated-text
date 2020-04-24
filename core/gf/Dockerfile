FROM haskell:8.8.3

RUN apt-get update && apt-get install wget python python-pip -y

RUN pip install backports.tempfile gunicorn pytest

RUN wget https://www.grammaticalframework.org/download/gf_3.10-2_amd64.deb
RUN dpkg -i gf_3.10-2_amd64.deb && rm -rf gf_3.10-2_amd64.deb

ADD *.py ./
ADD test_grammars test_grammars/

CMD ["gunicorn", "-b 0.0.0.0:8000", "server"]
