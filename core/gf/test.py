import pytest
import logging

from gf import generate_results

logger = logging.getLogger("GF")
logger.setLevel(logging.DEBUG)


def read_test_grammar(name):
    with open("test_grammars/" + name + ".gf") as file:
        output = file.read()
        file.close()
    return output


@pytest.fixture()
def api_post():
    return {
        "module": "Default",
        "instance": "Instance",
        "content": {
            "Default": read_test_grammar("Default"),
            "DefaultBody": read_test_grammar("DefaultBody"),
            "DefaultInstance": read_test_grammar("DefaultInstance"),
            "DefaultLex": read_test_grammar("DefaultLex"),
            "DefaultLexEng": read_test_grammar("DefaultLexEng")
        }
    }


def test_compile_grammar(api_post):
    data = api_post
    content = data["content"]
    name = data["module"]
    results = generate_results(name, content)
    print("Results: {}".format(results))
    assert len(results) > 0


def test_get_parse_tree(api_post):
    data = api_post
    content = data["content"]
    name = data["module"]
    (_, results) = generate_results(name, content)[0]
    print("Results: {}".format(results))
    assert results == {"something": True}
