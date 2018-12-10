import logging

import spacy

from collections import namedtuple

logger = logging.getLogger(__name__)


Token = namedtuple('token', [
    'text',
    'type',
    'start',
    'end',
    'part_of_speech',
    'is_stopword'
])


Entity = namedtuple('entity', ['text', 'type', 'start', 'end'])


def parse_sentences(text, model):
    logger.debug('Parsing sentences. Got "{0}" with model: {1}'
                 .format(text, model))
    nlp = spacy.load(model)
    doc = nlp(text)
    tokens_pos = dict({(token.i, (token.idx, token.idx + len(token.text),))
                       for token in doc})

    return [{'text': str(sent),
             'start': tokens_pos[sent.start][0],
             'end': tokens_pos[sent.end - 1][1],
             'type': 'SENTENCE'}
            for sent in doc.sents]


def parse_tokens(text, model):
    logger.debug('Parsing tokens. Got "{0}" with model: {1}'
                 .format(text, model))
    nlp = spacy.load(model)
    doc = nlp(text)

    return [{'text': token.text,
             'start': token.idx,
             'end': token.idx + len(token.text),
             'type': 'TOKEN'}
            for token in doc]


def parse_entities(text, model):
    logger.debug('Parsing entities. Got "{0}" with model: {1}'
                 .format(text, model))
    nlp = spacy.load(model)
    doc = nlp(text)
    return [{'text': ent.text,
             'start': ent.start_char,
             'end': ent.end_char,
             'type': ent.label_}
            for ent in doc.ents]


def is_inside(t1, t2):
    return (t2.start >= t1.start and t2.end <= t1.end)


def merge_tokens(u, v):
    skip = []

    def generate_entities(skip):
        for ent in u:
            for tok in v:
                if is_inside(ent, tok):
                    skip.append(tok)
                    yield Token(
                        text=ent.text,
                        type=ent.type,
                        start=ent.start,
                        end=ent.end,
                        part_of_speech=tok.part_of_speech,
                        is_stopword=tok.is_stopword
                    )

    entities = set(generate_entities(skip))
    logger.debug('Skiplist: {}'.format(skip))
    return sorted(
        list((set(v) - set(skip)).union(entities)),
        key=lambda x: x.start)


def parse_combined(text, model):
    logger.debug('Doing combined parse. Got "{0}" with model: {1}'
                 .format(text, model))

    nlp = spacy.load(model)
    doc = nlp(text)
    tokens_pos = dict({(token.i, (token.idx, token.idx + len(token.text),))
                       for token in doc})

    for sent in doc.sents:
        ents = [Entity(
                    text=ent.text,
                    start=ent.start_char,
                    end=ent.end_char,
                    type=ent.label_
                )
                for ent in sent.ents]

        tokens = [Token(
                      text=token.text,
                      start=token.idx,
                      end=token.idx + len(token.text),
                      type='TOKEN',
                      part_of_speech=token.pos_,
                      is_stopword=token.is_stop
                  )
                  for token in sent]

        yield {'text': str(sent),
               'type': 'SENTENCE',
               'start': tokens_pos[sent.start][0],
               'end': tokens_pos[sent.end - 1][1],
               'children':  merge_tokens(ents, tokens)
               }
