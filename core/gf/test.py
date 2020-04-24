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
    (_, result_objects) = generate_results(name, content)[0]
    tree = result_objects[0]["tree"][0]
    assert tree == {
  "fun": "Function01",
        "ann": "",
        "children": [
            {
                "fun": "Function02",
                "ann": "",
                "children": [
                    {
                        "fun": "Function03",
                        "ann": "",
                        "children": [
                            {
                                "fun": "Function04",
                                "ann": "",
                                "children": [
                                    {
                                        "fun": "Function05",
                                        "ann": "s",
                                        "children": [
                                            {
                                                "fun": "Function06",
                                                "ann": "s Pres Simul CPos (ODir False)",
                                                "children": [
                                                    "there",
                                                    "is",
                                                    "an",
                                                    {
                                                        "fun": "Function07",
                                                        "ann": "s Sg Nom",
                                                        "children": [
                                                            "item"
                                                        ],
                                                        "fid": 0,
                                                        "cat": "Operation07"
                                                    }
                                                ],
                                                "fid": 1,
                                                "cat": "Operation06"
                                            },
                                            "."
                                        ],
                                        "fid": 2,
                                        "cat": "Operation05"
                                    }
                                ],
                                "fid": 3,
                                "cat": "Frame04"
                            }
                        ],
                        "fid": 4,
                        "cat": "Frame03"
                    }
                ],
                "fid": 5,
                "cat": "Segment02"
            }
        ],
        "fid": 6,
        "cat": "DocumentPlan01"
    }
