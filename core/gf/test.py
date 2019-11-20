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

