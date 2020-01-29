import pytest
import server
import logging

logger = logging.getLogger("GF")
logger.setLevel(logging.DEBUG)


@pytest.fixture()
def api_post():
    return {
        "module":"Default",
        "instance":"Instance",
        "content": {
            "Default":"abstract Default = {\n    flags\n        startcat = DocumentPlan01 ;\n    cat\n        DocumentPlan01 ;\n        Segment02 ;\n        Amr03 ;\n    fun\n        Function01 : Segment02 -> DocumentPlan01 ;\n        Function02 : Amr03 -> Segment02 ;\n        Function03 : Amr03 ;\n}",
            "DefaultBody":"incomplete concrete DefaultBody of Default = open DefaultLex, LangFunctionsEng, ConceptNetEng, SyntaxEng, ParadigmsEng in {\n    lincat\n        DocumentPlan01, Segment02, Amr03 = {s: Str} ;\n    lin\n        Function01 Segment02 = {s = Segment02.s} ;\n        Function02 Amr03 = {s = Amr03.s} ;\n        Function03 = {s = (atLocation DictionaryItem04 Quote05 Quote06).s} ;\n}",
            "DefaultLex":"interface DefaultLex = {\n  oper\n    DictionaryItem04 : N ;\n    Quote05 : N ;\n    Quote06 : N ;\n}",
            "DefaultLexInstance":"resource DefaultLexInstance = open SyntaxEng, ParadigmsEng in {\n  oper\n    DictionaryItem04 : N = mkN \"arena\" | mkN \"place\" | mkN \"venue\" ;\n    Quote05 : N = mkN \"city centre\" ;\n    Quote06 : N = mkN \"Alimentum\" ;\n}",
            "DefaultInstance":"concrete DefaultInstance of Default = DefaultBody with \n  (DefaultLex = DefaultLexInstance);"
        }
}


def test_compile_grammar(api_post):
    data = api_post
    content = data["content"]
    name = data["module"]
    results = server.generate_results(name, content)
    print("Results: {}".format(results))
    assert len(results) > 0
