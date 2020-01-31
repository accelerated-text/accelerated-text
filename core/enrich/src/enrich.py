import logging
import random
import re

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

    def _encode(self, text, context):
        logger.info("Pre-encode: {}".format(text))
        return multi_replace(context, text)

    def _decode(self, text, context):
        logger.info("Pre-decode: {}".format(text))
        return multi_replace(inverse_dict(context), text)

    def enrich(self, sent, context, max_iters=3):
        tokens = tokenize(self._encode(sent, context))
        result = tokens
        iters = 0
        orig_len = len(tokens)
        while iters < max_iters:
            pos = random.randint(0, len(tokens) - 1)
            if len(result) < 3:
                op = random.choice([insert])
            elif len(result) <= orig_len:
                op = random.choice([insert, replace])
            else:
                op = random.choice([insert, remove, replace])
            try:
                result = op(result, pos, self.triplets)
                logger.debug("Using op: {0} on pos: {1}".format(op, pos))
                logger.debug("-> {}".format(result))
                iters += 1
            except OpRejected:
                logger.debug("Op Rejected.")

        return self._decode(" ".join(result), context)
