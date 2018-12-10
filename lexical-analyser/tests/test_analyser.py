import pytest

from core.analyser import merge_tokens, Token, Entity


@pytest.fixture
def entities():
    return list(map(lambda x: Entity(**x), [
    {
      "text": "John Jones",
      "start": 0,
      "end": 10,
      "type": "PERSON"
    },
    {
      "text": "Donald Trump",
      "start": 74,
      "end": 86,
      "type": "PERSON"
    }
  ]))


@pytest.fixture
def tokens():
    return list(map(lambda x: Token(**x, part_of_speech='dunno', is_stopword=False), [
    {
      "text": "John",
      "start": 0,
      "end": 4,
      "type": "TOKEN"
    },
    {
      "text": "Jones",
      "start": 5,
      "end": 10,
      "type": "TOKEN"
    },
    {
      "text": "did",
      "start": 11,
      "end": 14,
      "type": "TOKEN"
    },
    {
      "text": "something",
      "start": 15,
      "end": 24,
      "type": "TOKEN"
    },
    {
      "text": "very",
      "start": 25,
      "end": 29,
      "type": "TOKEN"
    },
    {
      "text": "bad",
      "start": 30,
      "end": 33,
      "type": "TOKEN"
    },
    {
      "text": ".",
      "start": 33,
      "end": 34,
      "type": "TOKEN"
    },
    {
      "text": "However",
      "start": 35,
      "end": 42,
      "type": "TOKEN"
    },
    {
      "text": ",",
      "start": 42,
      "end": 43,
      "type": "TOKEN"
    },
    {
      "text": "we",
      "start": 44,
      "end": 46,
      "type": "TOKEN"
    },
    {
      "text": "do",
      "start": 47,
      "end": 49,
      "type": "TOKEN"
    },
    {
      "text": "n't",
      "start": 49,
      "end": 52,
      "type": "TOKEN"
    },
    {
      "text": "care",
      "start": 53,
      "end": 57,
      "type": "TOKEN"
    },
    {
      "text": ".",
      "start": 57,
      "end": 58,
      "type": "TOKEN"
    },
    {
      "text": "On",
      "start": 59,
      "end": 61,
      "type": "TOKEN"
    },
    {
      "text": "Other",
      "start": 62,
      "end": 67,
      "type": "TOKEN"
    },
    {
      "text": "news",
      "start": 68,
      "end": 72,
      "type": "TOKEN"
    },
    {
      "text": ":",
      "start": 72,
      "end": 73,
      "type": "TOKEN"
    },
    {
      "text": "Donald",
      "start": 74,
      "end": 80,
      "type": "TOKEN"
    },
    {
      "text": "Trump",
      "start": 81,
      "end": 86,
      "type": "TOKEN"
    },
    {
      "text": "is",
      "start": 87,
      "end": 89,
      "type": "TOKEN"
    },
    {
      "text": "being",
      "start": 90,
      "end": 95,
      "type": "TOKEN"
    },
    {
      "text": "incompetent",
      "start": 96,
      "end": 107,
      "type": "TOKEN"
    },
    {
      "text": ".",
      "start": 107,
      "end": 108,
      "type": "TOKEN"
    }
  ]))

def test_merge_tokens(entities, tokens):
    g = merge_tokens(entities, tokens)
    assert len([token
                for token in g
                if token.text == 'John']) == 0
    assert len([token
                for token in g
                if token.text == 'Donald']) == 0
