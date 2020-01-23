resource ConceptNetEng = open SyntaxEng, ParadigmsEng, UtilsEng, (R=ResEng) in {

  oper -- atLocation
    SS : Type = {s : Str} ;

    -- There is a place in the LOCATION
    placeInLocation : N -> Adv -> N -> SS =
        \locationDictionary_N,locationData_Adv,objectRef_N ->
            (mkUtt (mkThereIsAThing (mkCN locationDictionary_N locationData_Adv) objectRef_N)) ;

    -- In the LOCATION there is a place
    inLocationPlace : N -> Adv -> N -> SS =
        \locationDictionary_N,locationData_Adv,objectRef_N ->
            (mkUtt (mkS locationData_Adv (mkS presentSimTemp positivePol (mkThereIsAThing locationDictionary_N objectRef_N)))) ;

    -- VENUE in the LOCATION
    venueInLocation : N -> Adv -> N -> SS =
        \locationDictionary_N,locationData_Adv,objectRef_N ->
            (mkUtt (mkThereIsAThing objectRef_N locationData_Adv)) ;

    atLocation : N -> N -> N -> SS =
        \lexicon,arg0,arg1 ->
            ((placeInLocation lexicon (mkInAdv arg0) arg1) |
             (inLocationPlace lexicon (mkInAdv arg0) arg1) |
             (venueInLocation lexicon (mkInAdv arg0) arg1)) ;

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

    locatedNear : N -> N -> N -> Prep -> N -> SS =
        \lexicon,location,venue,nearDictionary,nearData->
            (mkUtt ((placeInLocation lexicon (fullLocation_Adv location nearDictionary nearData) venue) |
                    (inLocationPlace lexicon (fullLocation_Adv location nearDictionary nearData) venue) |
                    (venueInLocation lexicon (fullLocation_Adv location nearDictionary nearData) venue))) ;


  oper -- capableOf 'Something that A can typically do is B.'

    capableOfImpl : V2 -> NP -> VP =
                    \action, result -> (mkVP action result);

    capableOf = overload {
      capableOf : V2 -> N -> A -> SS = 
                  \action, result, modifier ->
                  (mkS presentSimTemp positivePol
                       (mkCl (capableOfImpl action (mkNP (mkCN modifier result)))));

      capableOf : V2 -> NP -> SS = 
                  \action, result ->
                  (mkS presentSimTemp positivePol
                       (mkCl (capableOfImpl action result))) ;
    };
}