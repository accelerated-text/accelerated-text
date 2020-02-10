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


def test_tokenize():
    text = "Hello world, how are you?"
    result = tokenize(text)
    assert result == ["hello", "world", ",", "how", "are", "you", "?"]

@pytest.mark.parametrize(
    "tokens,pos,expected",
    [
        (["located", "in", "{area}", "near"], 2, ["located", "in", "the", "{area}", "near"]),
        (["located", "in", "{area}"], 2, ["located", "in", "the", "{area}"]),
        (["{name}", "located", "in", "{area}"], 1, ["{name}", "is", "located", "in", "{area}"])
    ],
)
def test_inserts(triplets, tokens, pos, expected):
    result = insert(tokens, pos, triplets)
    print("Final result: {}".format(result))
    assert result == expected


@pytest.mark.parametrize(
    "tokens,pos,expected",
    [
        (["on", "the", "{area}"], 0, ["in", "the", "{area}"]),
        (["located", "in", "riverside", "{area}"], 2, ["located", "in", "the", "{area}"]),
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

def test_placeholder_validate(triplets, nlp):
     orig = ["{name}", "is", "a", "in", "the", "{area}"]
     new =  ["{name}", "is", "a", "the", "{eat_type}", "{area}"]
     with pytest.raises(OpRejected):
         validate(orig, new, nlp)


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


def test_gramatical_structure_case_1(nlp):
    tokens = ["{name}", "is", "in", "the", "in", "our", "beautiful", "{area}"]
    pos = get_pos_signature(tokens, nlp)
    print(pos)
    assert not grammatically_valid_pos(pos)

    with pytest.raises(OpRejected):
        validate(tokens, tokens, nlp)
    
def test_inside_check():
    s1 = ["three", "four"]
    s2 = ["one", "two", "three", "four", "five"]
    assert inside(s1, s2)


def test_sentence_format():
    text = "test Text goes Here"
    assert format_result(text) == "Test Text goes Here."


def test_split_with_delim():
    text = "Hello, world! How are you?"
    results = list(split_with_delimiter(text, ".,!?"))

    assert results[0] == "Hello"
    assert results[1] == ","
    assert results[2] == "world"
    assert results[3] == "!"
    assert results[4] == "How are you"
    assert results[5] == "?"


def test_optimize_case_1(nlp):
    tokens = ["is", "a", "{eat_type}", "is", "located", "in", "the", "{area}"]
    result = optimize_grammar(tokens, nlp)
    assert result == ["is", "a", "{eat_type}", "located", "in", "the", "{area}"]


def test_optimize_case_2(nlp):
    tokens = ["is", "a", "{eat_type}", "located", "in", "the", "{area}", "near"]
    result = optimize_grammar(tokens, nlp)
    assert result == ["is", "a", "{eat_type}", "located", "in", "the", "{area}"]


@pytest.mark.full_test
class TestFullEnrich(object):
    @pytest.mark.parametrize("execution_number", range(5))
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

    @pytest.mark.parametrize("execution_number", range(5))
    def test_full_sentence_enrich_2(self, enricher, execution_number):
        print(execution_number)
        text = "Alimentum is restaurant near Burger King"
        accepted_results = set([
            "Alimentum is a restaurant near Burger King.",
            "Alimentum is a restaurant near the Burger King.",
            "Alimentum is friendly restaurant near the Burger King.",
            "Alimentum is restaurant near the Burger King.",
            "Alimentum a restaurant near the Burger King.",
        ])
        result = format_result(enricher.enrich(text, context={"Burger King": "{near}", "Alimentum": "{name}", "restaurant": "{eat_type}"}, max_iters=50))
        assert result != "Alimentum is restaurant near Burger King.", "Sentence is not enriched"
        assert result in accepted_results

    @pytest.mark.parametrize("execution_number", range(5))
    def test_full_sentence_enrich_3(self, enricher, execution_number):
        print(execution_number)
        text = "Starbucks is coffee shop"
        accepted_results = set([
            "Starbucks is a coffee shop."
        ])
        result = format_result(enricher.enrich(text, context={"Starbucks": "{name}", "coffee shop": "{eat_type}"}, max_iters=50))
        assert result != "Starbucks is coffee shop.", "Sentence is not enriched"
        assert result in accepted_results

    @pytest.mark.parametrize("execution_number", range(5))
    def test_full_sentence_enrich_4(self, enricher, execution_number):
        print(execution_number)
        text = "restaurant located in city center"
        accepted_results = set([
            "Restaurant located in the city center.",
            "Restaurant is located in the city center.",
            "Restaurant is located in city center.",
            "This establishment is located in the city center.",
            "This restaurant is located in the city center.",
            "Is a restaurant located in the city center.",
            "Is a restaurant located in city center.",
            "Friendly restaurant located in the city center.",
            "Friendly restaurant located on the city center.",
            "Is a friendly restaurant located in the city center.",
            "Is a friendly restaurant located on the city center.",
            "This restaurant is located in city center.",
            "Is a restaurant providing in the city center.",
            "A restaurant is located in the city center.",
            "A restaurant located in the city center.",
            "A restaurant is located in city center."
        ])
        result = format_result(enricher.enrich(text, context={"city center": "{area}", "restaurant": "{eat_type}"}, max_iters=50))
        assert result != "Restaurant located in city center", "Sentence is not enriched"
        assert result in accepted_results
