import pytest
import spacy

from src.utils import *


@pytest.fixture(scope="session")
def triplets():
    return load_example_triplets()


@pytest.fixture(scope="session")
def nlp():
    return spacy.load("en", parser=False, entity=False)


@pytest.mark.parametrize(
    "tokens,pos,expected",
    [
        (["located", "in", "{area}", "near"], 2, ["located", "in", "the", "{area}", "near"]),
        (["located", "in", "{area}"], 2, ["located", "in", "the", "{area}"]),
    ],
)
def test_inserts(triplets, tokens, pos, expected):
    result = insert(tokens, pos, triplets)
    print("Final result: {}".format(result))
    assert result == expected


@pytest.mark.parametrize(
    "tokens,pos,expected",
    [
        (["in", "the", "{area}"], 0, ["in", "the", "{area}"]),
        (["located", "in", "the", "{area}"], 2, ["located", "in", "the", "{area}"]),
    ],
)
def test_replace(tokens, pos, expected, triplets):
    result = replace(tokens, pos, triplets)

    print("Final result: {}".format(result))
    assert result == expected


def test_insert_validate(triplets, nlp):
    tokens = ["located", "in", "the", "{area}"]

    with pytest.raises(OpRejected):
        validate(tokens, insert(tokens, 2, triplets), nlp)


def test_pos_signature(nlp):
    tokens = ["located", "in", "the", "{area}"]
    result = get_pos_signature(tokens, nlp)
    print(result)
    assert result == ["VERB", "ADP", "VARIABLE"]


def test_pos_case_1(nlp):
    t1 = ["named", "{name}", "located", "in", "{area}", "is", "a", "not", "family-friendly"]
    t2 = ["named", "{name}", "located", "in", "{area}", "is", "a", "family-friendly"]

    assert get_pos_signature(t1, nlp) != get_pos_signature(t2, nlp)

    
def test_inside_check():
    s1 = ["three", "four"]
    s2 = ["one", "two", "three", "four", "five"]

    assert inside(s1, s2)

def test_sentence_format():
    text = "test Text goes Here"
    assert format_result(text) == "Test Text goes Here."
