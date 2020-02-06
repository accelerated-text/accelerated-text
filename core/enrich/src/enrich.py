import logging
import random
import re

from copy import copy
from functools import reduce

from src.utils import *
from src import pipeline


logger = logging.getLogger("Enrich")


def multi_replace(d, text):
  regex = re.compile("(%s)" % "|".join(map(re.escape, d.keys())))
  return regex.sub(lambda mo: d[mo.string[mo.start():mo.end()]], text)


def inverse_dict(d):
    return dict([(v, k) for (k, v) in d.items()])


class Enricher(object):
    def __init__(self):
        self.nlp = spacy.load("en", parser=False, entity=False)
        self.triplets = load_example_triplets()
        self.seqs = list(load_seq())

    def _encode(self, text, context):
        if context is None:
            return text
        logger.info("Pre-encode: {0}, Context: {1}".format(text, context))
        return multi_replace(context, text).lower()

    def _decode(self, text, context):
        if context is None:
            return text
        logger.info("Pre-decode: {0}, Context: {1}".format(text, context))
        return multi_replace(inverse_dict(context), text)

    def insert(self, tokens, pos):
        return insert(tokens, pos, self.triplets)

    def replace(self, tokens, pos):
        return replace(tokens, pos, self.triplets)

    def remove(self, tokens, pos):
        return remove(tokens, pos)

    def enrich(self, sent, context=None, max_iters=3, max_retries=10):
        tokens = tokenize(self._encode(sent, context))
        result = tokens
        iters = 0
        retries = 0

        pipe = [
            pipeline.random_pos,
            pipeline.random_op,
            pipeline.apply_op,
            pipeline.apply_validation,
            pipeline.get_result
        ]
        
        while iters < max_iters and retries < max_retries:
            try:
                result = reduce(lambda acc, p: p(*acc), pipe, (self, result))
            except OpRejected as rj:
                print("Rejected because: {}".format(rj))
                retries += 1
            else:
                iters += 1
                retries = 0
                print("Passed: {}".format(result))

        return self._decode(" ".join(result), context)
