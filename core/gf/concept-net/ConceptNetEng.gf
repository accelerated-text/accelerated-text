resource ConceptNetEng = open SyntaxEng, ParadigmsEng, UtilsEng in {

  oper -- atLocation
    SS : Type = {s : Str} ;

    -- There is a place in the LOCATION
    placeInLocation : N -> N -> N -> SS =
        \locationDictionary,locationData,objectRef ->
            (mkUtt (mkThereIsAThing (mkCN locationDictionary (mkInAdv locationData)) objectRef)) ;

    -- In the LOCATION there is a place
    inLocationPlace : N -> N -> N -> SS =
        \locationDictionary,locationData,objectRef ->
            (mkUtt (mkS (mkInAdv locationData) (mkS presentSimTemp positivePol (mkThereIsAThing locationDictionary objectRef)))) ;

    -- VENUE in the LOCATION
    venueInLocation : N -> N -> N -> SS =
        \locationDictionary,locationData,objectRef ->
            (mkUtt (mkThereIsAThing objectRef (mkInAdv locationData))) ;

    atLocation : N -> N -> N -> SS =
        \lexicon,arg0,arg1 ->
            ((placeInLocation lexicon arg0 arg1) | (inLocationPlace lexicon arg0 arg1) | (venueInLocation lexicon arg0 arg1)) ;

  oper -- hasProperty

    itHas : A -> N -> SS =
        \propertyName,object ->
            (mkS presentSimTemp positivePol (mkCl (mkNP object) propertyName)) ;

    hasProperty : A -> N -> SS =
        \lexicon,arg0 ->
            (mkUtt (itHas lexicon arg0)) ;

}