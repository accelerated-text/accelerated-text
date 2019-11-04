FROM haskell:8

RUN apt-get update
RUN apt-get install wget

RUN wget https://www.grammaticalframework.org/download/gf_3.10-2_amd64.deb
RUN dpkg -i gf_3.10-2_amd64.deb
