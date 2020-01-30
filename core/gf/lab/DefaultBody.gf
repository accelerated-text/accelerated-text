incomplete concrete DefaultBody of Default = open DefaultLex, LangFunctionsEng, ConceptNetEng, AtLocationEngImpl, SyntaxEng, ParadigmsEng in {
    lincat
        DocumentPlan01, Segment02, AtLocAMR, HasPropAMR = {s: Str};
        LocationDict, LocationData, VenueData = N ;
    lin
        Function01 Segment02 = {s = Segment02.s} ;
        Function02 atLocAMR hasPropAMR = {s = atLocAMR.s ++ hasPropAMR.s} ;
        Function03 loc area venue = {s = (atLocation loc area venue).s} ;
        Function04 = {s = (hasProperty Data08 Quote09 Data10).s} ;

        D01 = DictionaryItem04;
        D02 = Data05;
        D03 = Data06;
}
