import server

def test_compile_grammar():
    with open("test_grammars/GoodBook.gf", "r") as f:
        abstract = {"content": f.read()}

    with open("test_grammars/GoodBookEng.gf", "r") as f:
        inst = {"content": f.read(), "key": "Eng"}
        
    result = server.compile_grammar("GoodBook", abstract, [inst])
    print(result)
    assert result
    langs = result.languages
    assert len(langs) == 1
    assert "GoodBookEng" in langs

    expressions = server.generate_expressions(result)
    results = list([(k, server.generate_variants(expressions, concrete))
                    for k, concrete in result.languages])
    print(results)

    (_, r0) = results[0]
    assert set(r0) == set(
        "good {{TITLE}} is authored by {{AUTHOR}}",
        "good {{TITLE}} is written by {{AUTHOR}}",
        "{{AUTHOR}} is the author of excellent {{TITLE}}",
        "{{AUTHOR}} is the author of good {{TITLE}}",
    )
               

