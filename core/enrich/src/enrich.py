import logging
import random
import re

from copy import copy

from src.utils import *


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

    def enrich(self, sent, context=None, max_iters=3):
        tokens = tokenize(self._encode(sent, context))
        result = tokens
        prev_result = result
        iters = 0
        retries = 0
        orig_len = len(tokens)
        while iters < max_iters and retries < 10:
            pos = random.randint(0, len(tokens) - 1)
            if len(result) < 3:
                op = random.choice([insert])
            elif len(result) <= orig_len:
                op = random.choice([insert, replace])
            else:
                op = random.choice([insert, remove, replace])
            try:
                prev_result = copy(result)
                result = op(result, pos, self.triplets)
                validate(prev_result, result, self.nlp)
                # if not inside(result, self.seqs):
                #     raise OpRejected("'{}': Such result doesn't exist in our dataset. Consider it incorrect".format(result))
                logger.debug("Using op: {0} on pos: {1}".format(op, pos))
                logger.debug("-> {}".format(result))
                iters += 1
                retries = 0
            except OpRejected:
                logger.debug("Op Rejected.")
                retries += 1
                result = prev_result

        return self._decode(" ".join(result), context)
