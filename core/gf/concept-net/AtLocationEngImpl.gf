resource AtLocationEngImpl = open SyntaxEng, ParadigmsEng, UtilsEng, (R=ResEng) in {

  oper SS : Type = {s : Str} ;

  oper -- atLocation

    -- There is a place in the LOCATION
    placeInLocation : N -> Adv -> N -> SS =
        \locationDictionary_N,locationData_Adv,objectRef_N ->
            (mkS (mkThereIsAThing (mkCN locationDictionary_N locationData_Adv) objectRef_N)) ;

    -- In the LOCATION there is a place
    inLocationPlace : N -> Adv -> N -> SS =
        \locationDictionary_N,locationData_Adv,objectRef_N ->
            (mkS locationData_Adv (mkS presentSimTemp positivePol (mkThereIsAThing locationDictionary_N objectRef_N))) ;

    -- VENUE in the LOCATION
    venueInLocation : N -> Adv -> N -> SS =
        \locationDictionary_N,locationData_Adv,objectRef_N ->
            (mkS (mkThereIsAThing objectRef_N locationData_Adv)) ;

    atLocation_S = overload {
      atLocation_S : N -> N -> N -> S =
          \lexicon,location,venue ->
              (placeInLocation lexicon (mkInAdv location) venue) |
              (inLocationPlace lexicon (mkInAdv location) venue) |
              (venueInLocation lexicon (mkInAdv location) venue) ;

      atLocation_S : N -> N -> S =
          \location, venue -> (mkS (mkThereIsAThing venue (mkInAdv location)));
    };


  oper -- locatedNear

    fullLocation_Adv : N -> Prep -> N -> Adv =
        \locationData_N,nearDictionary_Prep,nearData_N ->
            (SyntaxEng.mkAdv (mkConj ",") (mkInAdv locationData_N) (SyntaxEng.mkAdv nearDictionary_Prep (mkNP the_Det nearData_N))) ;

    locatedNear : N -> N -> N -> Prep -> N -> SS =
        \lexicon,location,venue,nearDictionary,nearData->
              (placeInLocation lexicon (fullLocation_Adv location nearDictionary nearData) venue) |
              (inLocationPlace lexicon (fullLocation_Adv location nearDictionary nearData) venue) |
              (venueInLocation lexicon (fullLocation_Adv location nearDictionary nearData) venue) ;


}
