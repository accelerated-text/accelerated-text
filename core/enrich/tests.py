import pytest
import spacy

from src.utils import *
from src.enrich import Enricher


@pytest.fixture(scope="session")
def triplets():
    return load_example_triplets()


@pytest.fixture(scope="session")
def nlp():
    return spacy.load("en", parser=False, entity=False)

@pytest.fixture(scope="session")
def enricher():
    e = Enricher()
    return e


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
        result = insert(tokens, 2, triplets)
        print(result)
        validate(tokens, result, nlp)


def test_pos_signature(nlp):
    tokens = ["located", "in", "the", "{area}"]
    result = get_pos_signature(tokens, nlp)
    print(result)
    assert result == ["VERB", "ADP", "DET", "VARIABLE"]


def test_pos_case_1(nlp):
    t1 = ["named", "{name}", "located", "in", "{area}", "is", "a", "not", "family-friendly"]
    t2 = ["named", "{name}", "located", "in", "{area}", "is", "a", "family-friendly"]

    assert get_pos_signature(t1, nlp) != get_pos_signature(t2, nlp)


def test_pos_case_2(nlp):
    t1 = ["{name}", "located", "in", "{area}"]
    t2 = ["{name}", "in", "the", "{area}"]

    assert get_pos_signature(t1, nlp) != get_pos_signature(t2, nlp)

    
def test_inside_check():
    s1 = ["three", "four"]
    s2 = ["one", "two", "three", "four", "five"]

    assert inside(s1, s2)


def test_sentence_format():
    text = "test Text goes Here"
    assert format_result(text) == "Test Text goes Here."


@pytest.mark.full_test
class TestFullEnrich(object):
    @pytest.mark.parametrize("execution_number", range(20))
    def test_full_sentence_enrich_1(self, enricher, execution_number):
        print(execution_number)
        text = "Alimentum located in city center"
        accepted_results = set([
            "Alimentum is located in the city center.",
            "Alimentum is in the city center.",
            "Alimentum located in the city center.",
            "Alimentum is located in city center.",
        ])
        result = format_result(enricher.enrich(text, context={"city center": "{area}", "Alimentum": "{name}"}, max_iters=50))
        assert result != "Alimentum located in city center.", "Sentence is not enriched"
        assert result in accepted_results
