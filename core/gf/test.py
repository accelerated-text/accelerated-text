import pytest
import server

@pytest.fixture(scope="session")
def authorship_grammar():
    with open("test_grammars/Authorship.gf", "r") as f:
        abstract = {"content": f.read()}

    with open("test_grammars/AuthorshipEng.gf", "r") as f:
        inst = {"content": f.read(), "key": "Eng"}
        
    return server.compile_grammar("Authorship", abstract, [inst])


def test_compile_grammar(authorship_grammar):
    result = authorship_grammar
    print(result)
    assert result
    langs = result.languages
    assert len(langs) == 1
    assert "AuthorshipEng" in langs


def test_generation_results(authorship_grammar):
    expressions = server.generate_expressions(authorship_grammar)
    results = list([(k, server.generate_variants(expressions, concrete))
                    for k, concrete in authorship_grammar.languages.items()])
    print(results)

    (_, r0) = results[0]
    assert set(r0) == set([
        "good {{TITLE}} is authored by {{AUTHOR}}",
        "good {{TITLE}} is written by {{AUTHOR}}",
        "excellent {{TITLE}} is authored by {{AUTHOR}}",
        "excellent {{TITLE}} is written by {{AUTHOR}}",
        "{{AUTHOR}} is the author of excellent {{TITLE}}",
        "{{AUTHOR}} is the author of good {{TITLE}}",
    ])
               

