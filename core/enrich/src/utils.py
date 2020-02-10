import logging
import re

import spacy

from io import StringIO
from collections import Counter, defaultdict, MutableMapping
from functools import reduce

logger = logging.getLogger("Utils")


class OpRejected(RuntimeError):
    pass


def sliding_window(lst, n):
    for i in range(0, len(lst) - n):
        yield lst[i:i+n]


def inside(lst1, lst2):
    cond = lambda subset: subset == lst1
    n = len(lst1)
    for subset in sliding_window(lst2, n):
        if subset == lst1:
            return True

    return False



def load_seq():
    with open("data/text_raw.txt", "r") as f:
        return [t.replace(".", "").strip()
                for t in tokenize(f.read())]


def load_example_triplets():
    with open("data/text_raw.txt", "r") as f:
        return Counter(ngram(load_seq(), n=3))


def split_with_delimiter(text, delimiters):
    def get_result(b):
        result = b.getvalue().strip()
        b.truncate(0)
        b.seek(0)
        return result
        
    buff = StringIO()
    for c in text:
        if c in delimiters:
            result = get_result(buff)
            if result.strip() != "":
                yield result
            yield c
        else:
            buff.write(c)

    if buff.tell() > 0:
        yield get_result(buff)


def tokenize(text):
    return [ts
            for t in re.split(r"\s", text)
            for ts in split_with_delimiter(t, ".,:!?")
            if ts.strip() != ""]


def ngram(g, n):
    if n > 1:
        return zip(*[g[i:] for i in range(n)])
    else:
        return g



def left_pair(k1, k2, triplets):
    """
    We have left word and searching for right
    (<our_words>, variants...)
    """
    rights = [(r, v)
              for (l, m, r), v in triplets.items() 
              if l == k1 and m == k2]
    total = sum([v for (_, v) in rights])
    return sorted([(r, v/total) for (r, v) in rights], key=lambda x: -x[1])


def right_pair(k1, k2, triplets):
    """
    We have right word and searching for left
    (variants..., <our_words>)
    """
    lefts = [(l, v)
              for (l, m, r), v in triplets.items() 
              if m == k1 and r == k2]
    total = sum([v for (_, v) in lefts])
    return sorted([(r, v/total) for (r, v) in lefts], key=lambda x: -x[1])


def middle(k1, k2, triplets):
    """
    Searching for a word in between
    """
    middles = [(m, v)
                for (l, m, r), v in triplets.items() 
                if l == k1 and r == k2]
    total = sum([v for (_, v) in middles])
    return sorted([(r, v/total) for (r, v) in middles], key=lambda x: -x[1])


def window(lefts, rights, triplets):
    logger.debug("Lefts: {0}, Rights: {1}".format(lefts, rights))
    if len(lefts) == 1 and len(rights) == 1:
        return middle(lefts[0], rights[0], triplets)
    elif len(lefts) == 0 and len(rights) == 1:
        return []
    elif len(lefts) == 1 and len(rights) == 1:
        return []
    elif len(lefts) == 0 and len(rights) == 2:
        return right_pair(rights[0], rights[1], triplets)
    elif len(lefts) == 2 and len(rights) == 0:
        return left_pair(lefts[0], lefts[1], triplets)
    else:
        logger.debug("Lefts: {0}, Rights: {1}".format(lefts, rights))
        results = defaultdict(list)
        middle_results = [(k, v)
                          for (k, v) in middle(lefts[-1], rights[0], triplets)
                          if k not in lefts+rights]
        logger.debug("Middle results: {}".format(middle_results))
        for (m, p) in middle_results:
            results[m].append(p)
            
        if len(lefts) == 2:
            logger.debug("Handling left: {}".format(lefts))
            left_results = [(k, v)
                            for (k, v) in left_pair(lefts[0], lefts[1], triplets)
                            if k != rights[0]]
            logger.debug("Left results: {}".format(left_results))
            for (l, p) in left_results:
                results[l].append(p)
                
        if len(rights) == 2:
            logger.debug("Handling right: {}".format(rights))
            right_results = [(k, v)
                             for (k, v) in right_pair(rights[0], rights[1], triplets)
                             if k != lefts[-1]]
            logger.debug("Right results: {0}, lefts: {1}".format(right_results, lefts[-1]))
            for (r, p) in right_results:
                results[r].append(p)

        def avg(lst):
            n = len(lst)
            return sum(lst)/n

        results_normalized = [(k, avg(v))for k, v in results.items()]
                
        return sorted(results_normalized, key=lambda x: -x[1])[:10]
    

def is_placeholder(t):
    return t.startswith("{")


def filter_placeholders(lst):
    return list([(m, p) for (m, p) in lst if not is_placeholder(m)])


def get_pos_signature(tokens, nlp=None):
    doc = nlp(" ".join(tokens))
    pattern = [(t.text, t.pos_) for t in doc]

    def gen():
        prev = None
        for (t, p) in pattern:
            if prev == "placeholder_start":
                yield "VARIABLE"
                prev = None
                continue
            
            if t == "{":
                prev = "placeholder_start"
            elif t == "}":
                prev = "placeholder_end"
            else:
                prev = None
                yield p

    return list(gen())

def grammatically_valid_pos(pos):
    pairs = list(ngram(pos, n=2))
    if any([p1 == "DET" and p2 == "DET" for (p1, p2) in pairs]):
        logger.debug("DET before DET")
        return False

    if any([p1 == "AUX" and p2 == "AUX" for (p1, p2) in pairs]):
        logger.debug("AUX before AUX")
        return False
    
    if any([p1 == "DET" and p2 == "VERB" for (p1, p2) in pairs]):
        logger.debug("DET before VERB")
        return False

    if any([p1 == "DET" and p2 == "ADP" for (p1, p2) in pairs]):
        logger.debug("DET before ADP")
        return False

    if any([p1 == "DET" and p2 == "AUX" for (p1, p2) in pairs]):
        logger.debug("DET before AUX")
        return False

    if any([p1 == "ADV" and p2 == "PRON" for (p1, p2) in pairs]):
        logger.debug("ADV before PRON")
        return False

    if any([p1 == "ADJ" and p2 == "PRON" for (p1, p2) in pairs]):
        logger.debug("ADJ before PRON")
        return False

    if any([p1 == "ADJ" and p2 == "DET" for (p1, p2) in pairs]):
        logger.debug("ADJ before DET")
        return False

    if any([p1 == "DET" and p2 == "PRON" for (p1, p2) in pairs]):
        logger.debug("DET before PRON")
        return False

    if any([p1 == "DET" and p2 == "ADJ" for (p1, p2) in pairs]):
        logger.debug("DET before ADJ")
        return False

    if any([p1 == "ADP" and p2 == "AUX" for (p1, p2) in pairs]):
        logger.debug("ADP before AUX")
        return False

    if any([(p1 == "ADV" and p2 == "ADP") or (p1 == "ADP" and p2 == "ADV")
            for (p1, p2) in pairs]):
        logger.debug("ADV and ADP in pair")
        return False
    return True

def compare_pos_signatures(left, right):
    def filter_fn(p):
        return p not in ["DET", "ADV", "AUX", "ADJ"]

    left_side = list(filter(filter_fn, left))
    right_side = list(filter(filter_fn, right))
    return left_side == right_side


def validate(original, new, nlp):
    placeholders_original = sum([1 for t in original if is_placeholder(t)])
    placeholders_new = sum([1 for t in new if is_placeholder(t)])
    logger.debug("Orig: {0}, New: {1}".format(placeholders_original, placeholders_new))
    if placeholders_original != placeholders_new :
        raise OpRejected("New placeholders introduced or removed")

    orig_pos = get_pos_signature(original, nlp)
    new_pos = get_pos_signature(new, nlp)

    if not compare_pos_signatures(orig_pos, new_pos):
        raise OpRejected("Lexical Structure changed too much")

    if not grammatically_valid_pos(new_pos):
        raise OpRejected("Invalid gramatical structure")
    return new


def insert(t, pos, triplets):
    left_side = t[max(0, pos-2):pos]
    right_side = t[pos:pos+2]

    results = filter_placeholders(window(left_side, right_side, triplets))
    if len(results) == 0:
        raise OpRejected("Couldn't insert anything")
    else:
        (m, _) = results[0]
        logger.debug("Left side: {0}, Right side: {1}, Variants: {2}".format(t[:pos], t[pos:], results))
        return t[:pos] + [m] + t[pos:]

def remove(t, pos):
    if is_placeholder(t[pos]):
        raise OpRejected("Don't remove placeholders")

    result = t
    del result[pos]
    return result


def replace(t, pos, triplets):
    current = t[pos]
    if is_placeholder(current):
        # Don't replace placeholders
        raise OpRejected("Don't replace placeholders")

    new = t
    
    left_side = t[:pos][-2:]
    right_side = t[pos+1:][:2]
    
    results = [(m, p)
               for (m, p) in filter_placeholders(window(left_side, right_side, triplets))
               if m != current]
    if len(results) == 0:
        raise OpRejected("Nothing to replace")
    else:
        (m, p) = results[0]
        logger.debug("{0} -> {1} in Sentence: {2}. P={3}".format(current, m, t, p))
        if p < 0.20:
            raise OpRejected("Probability of this change is too low ({0})".format(p))
        new[pos] = m
        return new

 
def knuth_morris_pratt(source, pattern):
    '''Yields all starting positions of copies of the pattern in the text.
Calling conventions are similar to string.find, but its arguments can be
lists or iterators, not just strings, it returns all matches, not just
the first one, and it does not need the whole text in memory at once.
Whenever it yields, it will have read the text exactly up to and including
the match that caused the yield.'''

    # allow indexing into pattern and protect against change during yield
    pattern = list(pattern)

    # build table of shift amounts
    shifts = [1] * (len(pattern) + 1)
    shift = 1
    for pos in range(len(pattern)):
        while shift <= pos and pattern[pos] != pattern[pos-shift]:
            shift += shifts[pos-shift]
        shifts[pos+1] = shift

    # do the actual search
    startPos = 0
    matchLen = 0
    for c in source:
        while matchLen == len(pattern) or \
              matchLen >= 0 and pattern[matchLen] != c:
            startPos += shifts[matchLen]
            matchLen -= shifts[matchLen]
        matchLen += 1
        if matchLen == len(pattern):
            yield startPos


def optimize_grammar(tokens, nlp):
    def case_1(t):
        pos = get_pos_signature(tokens, nlp)
        example = ["AUX", "DET", "VARIABLE", "AUX", "VERB"]
        for match_position in knuth_morris_pratt(pos, example):
            # Our defined pattern starts at `match_position`
            # we want to remove second AUX, which is 4th token
            return case_1(remove(t, match_position + 3))
        return t

    def case_2(t):
        pos = get_pos_signature(tokens, nlp)
        if pos[-1] in ["ADV", "ADJ", "DET", "AUX", "SCONJ"]:
            # If sentence is ending with these, it is likelly incorrect
            return case_2(remove(t, len(pos)-1))
        return t

    return reduce(lambda acc, p: p(acc), [case_1, case_2], tokens)
  
 
def format_result(text):
    def strip(t):
        return t.strip()
    
    def capitalize_first(t):
        return t[0].upper() + t[1:]

    def end_with_dot(t):
        return t + "."

    def remove_whitespace_before_punct(t):
        return re.sub(r"(\s)([.,!?:])", r"\2", t)

    pipeline = [strip, capitalize_first, remove_whitespace_before_punct, end_with_dot]
    return reduce(lambda t, fn: fn(t), pipeline, text)


class CaseInsensitiveKey(object):
    def __init__(self, value):
        if isinstance(value, CaseInsensitiveKey):
            self.value = value.value
        else:
            self.value = value
        
    def __eq__(self, other):
        if isinstance(other, CaseInsensitiveKey):
            return self.value.lower() == other
        else:
            return self.value.lower() == other.lower()

    def __hash__(self):
        return hash(repr(self))

    def __repr__(self):
        return self.value.lower()


class CaseInsensitiveDict(MutableMapping):
    def __init__(self, *args, **kwargs):
        self.store = dict()
        self.update(dict(*args, **kwargs))

    def __getitem__(self, key):
        return self.store[self.__keytransform__(key)]

    def __setitem__(self, key, value):
        self.store[self.__keytransform__(key)] = value

    def __delitem__(self, key):
        del self.store[self.__keytransform__(key)]

    def __iter__(self):
        return iter(self.store)

    def __len__(self):
        return len(self.store)

    def __keytransform__(self, key):
        return CaseInsensitiveKey(key)
