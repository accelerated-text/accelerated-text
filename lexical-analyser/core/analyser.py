import logging

import spacy


logger = logging.getLogger(__name__)


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


def parse_combined(text, model):
    logger.debug('Doing combined parse. Got "{0}" with model: {1}'
                 .format(text, model))
    
    nlp = spacy.load(model)
    doc = nlp(text)
    tokens_pos = dict({(token.i, (token.idx, token.idx + len(token.text),))
                       for token in doc})

    for sent in doc.sents:
        ents = [{'text': ent.text,
                 'start': ent.start_char,
                 'end': ent.end_char,
                 'type': ent.label_,
                 'attributes': []}
                            for ent in sent.ents]
        
        tokens = [{'text': token.text,
                   'start': token.idx,
                   'end': token.idx + len(token.text),
                   'type': 'TOKEN',
                   'attributes': []}
                  for token in sent]
        yield {'text': str(sent),
               'type': 'SENTENCE',
               'start': tokens_pos[sent.start][0],
               'end': tokens_pos[sent.end - 1][1],
               'children':  list(ents) + list(tokens)
               }
