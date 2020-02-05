resource LocatedNearEng = open SyntaxEng, ParadigmsEng, UtilsEng, AtLocationEng in {

  oper -- locatedNear

    fullLocation_Adv : N -> Prep -> N -> Adv =
        \locationData_N,nearDictionary_Prep,nearData_N ->
            (SyntaxEng.mkAdv (mkConj ",") (mkInAdv locationData_N) (SyntaxEng.mkAdv nearDictionary_Prep (mkNP the_Det nearData_N))) ;

    locatedNear : N -> N -> N -> Prep -> N -> S =
        \lexicon,location,venue,nearDictionary,nearData->
            (placeInLocation lexicon (fullLocation_Adv location nearDictionary nearData) venue) |
            (inLocationPlace lexicon (fullLocation_Adv location nearDictionary nearData) venue) |
            (venueInLocation lexicon (fullLocation_Adv location nearDictionary nearData) venue) ;

}