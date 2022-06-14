FROM ubuntu:20.04

RUN apt-get update && apt-get install wget python unzip curl libtinfo5 libffi7 libatomic1 -y

RUN curl https://bootstrap.pypa.io/pip/2.7/get-pip.py | python

RUN pip install backports.tempfile gunicorn pytest

RUN wget https://github.com/GrammaticalFramework/gf-core/releases/download/3.11/gf-3.11-ubuntu-20.04.deb
RUN dpkg -i gf-3.11-ubuntu-20.04.deb && rm gf-3.11-ubuntu-20.04.deb || true

RUN wget https://github.com/GrammaticalFramework/gf-rgl/releases/download/20201114/gf-rgl-20201114.zip
RUN unzip gf-rgl-20201114.zip -d /opt && rm gf-rgl-20201114.zip || true
ENV GF_LIB_PATH=/opt/gf-rgl-20201114-test

RUN mkdir /grammars && gf --output-dir=/grammars -n LangEng -make alltenses/LangEng.gfo

ADD *.py ./
ADD test_grammars test_grammars/

CMD ["gunicorn", "-b 0.0.0.0:8000", "server", "--access-logfile", "-", "--error-logfile", "-"]
