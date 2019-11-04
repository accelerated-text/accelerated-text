FROM haskell:8
RUN mkdir -p /opt/gf
WORKDIR /opt/gf

RUN apt-get update
RUN apt-get install wget python3 python3-dev autoconf automake libtool make -y
RUN ln -s /usr/bin/python3.5 /usr/bin/python

RUN wget https://www.grammaticalframework.org/download/gf_3.10-2_amd64.deb
RUN dpkg -i gf_3.10-2_amd64.deb
RUN rm -rf gf_3.10-2_amd64.deb

RUN wget https://github.com/GrammaticalFramework/gf-core/archive/GF-3.10.tar.gz
RUN tar -xzf GF-3.10.tar.gz && rm GF-3.10.tar.gz

RUN mkdir -p /usr/src/runtime && cp -R gf-core-GF-3.10/src/runtime/c /usr/src/runtime/c && cp -R gf-core-GF-3.10/src/runtime/python /usr/src/runtime/python && rm -rf gf-core-GF-3.10

RUN cd /usr/src/runtime/c && autoreconf -i && ./configure && make && make install
RUN cd /usr/src/runtime/python && python setup.py install

ADD integrations/gf/server.py server.py

CMD ["/usr/bin/python", "server.py"]
