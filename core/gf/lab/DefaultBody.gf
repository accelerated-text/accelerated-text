incomplete concrete DefaultBody of Default = open DefaultLex, LangFunctionsEng, ConceptNetEng, SyntaxEng, ParadigmsEng in {
    lincat
        DocumentPlan01, Segment02, Amr03, Amr07 = {s: Str} ;
    lin
        Function01 Segment02 = {s = Segment02.s} ;
        Function02 Amr03 Amr07 = {s = Amr03.s ++ Amr07.s} ;
        Function03 = {s = (atLocation DictionaryItem04 Data05 Data06).s} ;
        Function04 = {s = (hasProperty Data08 Quote09 Data10).s} ;
}
