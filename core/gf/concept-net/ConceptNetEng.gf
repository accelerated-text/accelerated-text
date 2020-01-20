resource ConceptNetEng = open SyntaxEng, ParadigmsEng, UtilsEng in {

  oper -- atLocation
    SS : Type = {s : Str} ;

    -- There is a place in the LOCATION
    placeInLocation : N -> N -> N -> SS =
        \locationDictionary_N,locationData_N,objectRef_N ->
            (mkUtt (mkThereIsAThing (mkCN locationDictionary_N (mkInAdv locationData_N)) objectRef_N)) ;

    -- In the LOCATION there is a place
    inLocationPlace : N -> N -> N -> SS =
        \locationDictionary_N,locationData_N,objectRef_N ->
            (mkUtt (mkS (mkInAdv locationData_N) (mkS presentSimTemp positivePol (mkThereIsAThing locationDictionary_N objectRef_N)))) ;

    -- VENUE in the LOCATION
    venueInLocation : N -> N -> N -> SS =
        \locationDictionary_N,locationData_N,objectRef_N ->
            (mkUtt (mkThereIsAThing objectRef_N (mkInAdv locationData_N))) ;

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

  oper -- locatedNear

    fullLocation_Adv : N -> Prep -> N -> Adv =
        \locationData_N,nearDictionary_Prep,nearData_N ->
            (SyntaxEng.mkAdv (mkConj ",") (mkInAdv locationData_N) (SyntaxEng.mkAdv nearDictionary_Prep (mkNP the_Det nearData_N))) ;

    locatedNear : N -> N -> Prep -> N -> SS =
        \lexicon,arg0,arg1,arg2 ->
            (mkUtt (mkCN lexicon (fullLocation_Adv arg0 arg1 arg2))) ;

}