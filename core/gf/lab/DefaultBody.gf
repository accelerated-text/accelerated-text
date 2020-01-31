incomplete concrete DefaultBody of Default = open DefaultLex, LangFunctionsEng, ConceptNetEng, AtLocationEngImpl, HasPropertyEngImpl, SyntaxEng, ParadigmsEng in {
    lincat
        DocumentPlan01, Segment02 = {s: Str};
        AtLocationSnt, HasPropertySnt = S;
        PropertyMod = A;
        LocationDict, LocationData, VenueData = N ;
        Property = A;
        Polarity = Pol;
    lin
        Function01 Segment02 = {s = Segment02.s} ;

        Function02 atLocSnt hasPropSnt = {s = atLocSnt.s ++ "." ++ hasPropSnt.s} ;
        Function021 atLocSnt hasPropMod = {s = atLocSnt.s} ;

        Function03 loc area venue = atLocation_S loc area venue ;
        Function031 loc area venue mod = atLocation_S loc area venue mod;
        Function04 = hasProperty Data08 Quote09 Data10 ;

        FunctionAMRHasProp property polarity = hasProperty_A property polarity;

        D01 = DictionaryItem04;
        D02 = Data05;
        D03 = Data06;
        D04 = Data10; -- polarity
        D05 = Quote09; -- FF
}
