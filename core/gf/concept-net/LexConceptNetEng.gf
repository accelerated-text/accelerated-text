instance LexConceptNetEng of LexConceptNet = open SyntaxEng, ParadigmsEng in {
  oper
    Result : Type = Cl ;


    place_N = mkN ("place" | "venue" | "arena") ;
    place_Cl = (mkCl (mkCN place_N));
    location_ARG = mkNP the_Det (mkN "{{location}}") ;
    venue_N = mkN "{{venue}}";
    venue_CN = mkCN venue_N;
    venue_ARG = mkNP venue_N ;
    venue_NP = venue_ARG;
    venue_A = mkA "{{venue}}";

    present_simultaneous_temp = mkTemp presentTense simultaneousAnt;

    -- in the LOCATION
    in_loc = SyntaxEng.mkAdv in_Prep location_ARG;

    -- There is a place in the LOCATION
    place_in_location = mkCN (mkCN place_N in_loc) venue_NP;
    -- In the LOCATION there is a place
    in_location_place = mkS in_loc (mkS present_simultaneous_temp positivePol place_Cl); 

    -- VENUE in the LOCATION
    venue_in_location = mkCN venue_CN in_loc;


    place_phrase = mkCl place_in_location;
    in_loc_phrase = mkCl venue_in_location;

    mkSentence = overload {
              mkSentence : N -> Adv -> VP -> Result = \w1,w2,w3  -> mkCl (mkSC (mkS (mkCl w1))) (mkVP w3 w2) ;

    };

    mkAtLocation : NP -> Adv -> Cl = \v,l -> mkSentence place_N l (mkVP (mkV2 "name") v) ;

}

-- Lang> p "in the city there is a city"
-- PhrUtt NoPConj (UttS
-- (AdvS (PrepNP in_Prep (DetCN (DetQuant DefArt NumSg) (UseN city_N))) (UseCl (TTAnt TPres ASimul) PPos (ExistNP (DetCN (DetQuant IndefArt NumSg) (UseN city_N))))))
-- NoVoc