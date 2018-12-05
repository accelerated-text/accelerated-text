from ibbd/spacy

COPY requirements.txt requirements.txt
RUN pip install -r requirements.txt

COPY api/ api/
COPY core/ core/
COPY data/ data/

EXPOSE 8000

CMD ["/usr/local/bin/gunicorn", "api.wsgi", "-b", "0.0.0.0:8000"]
