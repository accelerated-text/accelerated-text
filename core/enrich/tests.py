import pytest

from collections import Counter, defaultdict

from src import insert, replace, remove, OpRejected, tokenize, ngram, get_pos_signature


@pytest.fixture(scope="session")
def triplets():
    with open("data/text_raw.txt", "r") as f:
        return Counter(ngram([t.replace(".", "").strip()
                              for t in tokenize(f.read())], n=3))


def test_insert_between_4(triplets):
    tokens = ["located", "in", "{area}", "near"]
    result = insert(tokens, 2, triplets)
    print("Final result: {}".format(result))
    assert result == ["located", "in", "the", "{area}", "near"]


def test_insert_between_3(triplets):
    tokens = ["located", "in", "{area}"]
    result = insert(tokens, 2, triplets)
    print("Final result: {}".format(result))
    assert result == ["located", "in", "the", "{area}"]


def test_replace(triplets):
    tokens = ["in", "the", "{area}"]
    result = replace(tokens, 0, triplets)

    print("Final result: {}".format(result))
    assert result == ["in", "the", "{area}"]


def test_insert_case_1(triplets):
    tokens = ["located", "in", "the", "{area}"]

    with pytest.raises(OpRejected):
        insert(tokens, 2, triplets)


def test_replace_case_1(triplets):
    tokens = ["located", "in", "the", "{area}"]
    result = replace(tokens, 2, triplets)

    print("Final result: {}".format(result))
    assert result != ["located", "in", "in", "{area}"]

def test_replace_case_2(triplets):
    tokens = ["restaurant", "called", "{name}", "located", "in", "{area}", "is", "not", "family-friendly"]
    result = replace(tokens, 6, triplets)

    print("Final result: {}".format(result))


def test_pos_signature():
    tokens = ["located", "in", "the", "{area}"]
    result = get_pos_signature(tokens)
    print(result)
    assert result == ["VERB", "ADP", "VARIABLE"]

def test_pos_case_1():
    t1 = ["named", "{name}", "located", "in", "{area}", "is", "a", "not", "family-friendly"]
    t2 = ["named", "{name}", "located", "in", "{area}", "is", "a", "family-friendly"]

    assert get_pos_signature(t1) != get_pos_signature(t2)

    
