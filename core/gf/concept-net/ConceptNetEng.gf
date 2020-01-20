resource ConceptNetEng = open SyntaxEng, ParadigmsEng, UtilsEng in {

  oper -- atLocation
    SS : Type = {s : Str} ;

    -- There is a place in the LOCATION
    place_in_location : N -> N -> N -> SS =
        \locationDictionary,locationData,objectRef ->
            {s = (mkUtt (mkThereIsAThing (mkCN locationDictionary (mkInAdv locationData)) objectRef)).s} ;

    -- In the LOCATION there is a place
    in_location_place : N -> N -> N -> SS =
        \locationDictionary,locationData,objectRef ->
            {s = (mkUtt (mkS (mkInAdv locationData) (mkS mkPresentSimTemp positivePol (mkThereIsAThing locationDictionary objectRef)))).s} ;

    -- VENUE in the LOCATION
    venue_in_location : N -> N -> N -> SS =
        \locationDictionary,locationData,objectRef ->
            {s = (mkUtt (mkThereIsAThing objectRef (mkInAdv locationData))).s} ;

    atLocation : N -> N -> N -> SS =
        \lexicon,arg0,arg1 ->
            ((place_in_location lexicon arg0 arg1) | (in_location_place lexicon arg0 arg1) | (venue_in_location lexicon arg0 arg1)) ;

  oper -- hasProperty

    itHas : A -> N -> SS =
      \propertyName,object ->
        mkS presentSimTemp positivePol (mkCl (mkNP object) propertyName) ;

    hasProperty : A -> N -> SS =
      \lexicon,arg0 ->
        (mkUtt (itHas lexicon arg0)) ;

}