incomplete concrete SimpleBody of Simple = open Syntax, Grammar, SimpleLex, ParadigmsEng in {
    lincat
        DocumentPlan01, Segment02, Frame03, Frame04 = Str ;
        Operation11 = N ;
        Operation06 = S ;
        Operation10 = NP ;
        Operation07 = Cl ;
        Operation05 = Text ;
        Operation08 = V ;
    lin
        Function01 Segment02 = Segment02 ;
        Function02 Frame03 = Frame03 ;
        Function03 Frame04 = Frame04 ;
        Function04 Operation05 = Operation05.s ;
        Function05 Operation06 = Syntax.mkText Operation06 ;
        Function06 Operation07 = Syntax.mkS Operation07 ;
        Function07 Operation10 Operation08 = Syntax.mkCl Operation10 Operation08 ;
        Function08 = ParadigmsEng.mkV DictionaryItem09 ;
        Function09 Operation11 = Syntax.mkNP Operation11 ;
        Function10 = ParadigmsEng.mkN DictionaryItem12 ;
}