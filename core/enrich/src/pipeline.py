import random
import logging

from copy import copy

from src.utils import validate, optimize_grammar

logger = logging.getLogger(__name__)


def random_pos(ctx, tokens):
    return (ctx, tokens, random.randint(0, len(tokens) - 1))


def random_op(ctx, tokens, pos):
    if len(tokens) < 3:
        op = random.choice([ctx.insert])
    else:
        # op = random.choice([ctx.insert, ctx.remove, ctx.replace])
        op = random.choice([ctx.insert, ctx.replace])
    return (ctx, tokens, pos, op)


def apply_op(ctx, tokens, pos, op):
    prev = copy(tokens)
    new = op(copy(tokens), pos)
    logger.debug("Applying: {0} pos: {1}. Prev: {2}, New: {3}".format(op, pos, prev, new))
    return (ctx, new, prev)


def apply_validation(ctx, new, prev):
    validate(prev, new, ctx.nlp)
    return (ctx, new)


def optimize(ctx, new):
    return (ctx, optimize_grammar(new, ctx.nlp))


def get_result(ctx, new):
    return new
