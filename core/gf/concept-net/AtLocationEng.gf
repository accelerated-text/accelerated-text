resource AtLocationEng = open ParadigmsEng, SyntaxEng, UtilsEng in {

  oper -- atLocation

    -- There is a place in the LOCATION
    placeInLocation : N -> Adv -> N -> S =
        \locationDictionary_N,locationData_Adv,objectRef_N ->
            (mkS (mkThereIsAThing (mkCN locationDictionary_N locationData_Adv) objectRef_N)) ;

    -- In the LOCATION there is a place
    inLocationPlace : N -> Adv -> N -> S =
        \locationDictionary_N,locationData_Adv,objectRef_N ->
            (mkS locationData_Adv (mkS presentSimTemp positivePol (mkThereIsAThing locationDictionary_N objectRef_N))) ;

    -- VENUE in the LOCATION
    venueInLocation : N -> Adv -> N -> S =
        \locationDictionary_N,locationData_Adv,objectRef_N ->
            (mkS (mkThereIsAThing objectRef_N locationData_Adv)) ;

    atLocation : N -> N -> N -> S =
        \lexicon,location,venue ->
            (placeInLocation lexicon (mkInAdv location) venue) |
            (inLocationPlace lexicon (mkInAdv location) venue) |
            (venueInLocation lexicon (mkInAdv location) venue) ;

}