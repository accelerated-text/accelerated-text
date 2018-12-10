import sys
import os.path
import logging
import pickle
import json

from collections import defaultdict

logger = logging.getLogger(__name__)

LANG_TO_MODEL = {}


def build_moby():
    path = 'data/mobythes.aur'
    logger.info('Reading Moby thesaurus DB')
    with open(path, 'r') as f:
        raw = f.read()
        records = raw.split('\n')

    logger.info('Building Graph')
    d = {}
    for record in records:
        items = record.split(',')
        for item in items:
            d[item] = set(items) - set([item])

    logger.info('Done')
    with open('data/moby.pkl', 'wb') as f:
        pickle.dump(d, f)


def read_moby():
    if not os.path.exists('data/moby.pkl'):
        logger.info('No Moby model. Build it')
        sys.exit(1)

    with open('data/moby.pkl', 'rb') as f:
        return pickle.load(f)


def read_wordnet():
    if not os.path.exists('data/en_thesaurus.jsonl'):
        logger.info('No WordNet model found')
        sys.exit(1)

    db = defaultdict(list)
    with open('data/en_thesaurus.jsonl', 'r') as f:
        docs = [json.loads(line)
                for line in f.readlines()]

    for d in docs:
        db[d['word']].append({
            'key': d['key'],
            'synonyms': d['synonyms'],
            'pos': d['pos']
        })

    return db


LANG_TO_MODEL['en'] = read_wordnet()


def get_thesaurus(word, lang='en'):
    model = LANG_TO_MODEL[lang]
    return model[word]
