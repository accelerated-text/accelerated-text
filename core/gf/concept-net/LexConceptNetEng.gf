instance LexConceptNetEng of LexConceptNet = open SyntaxEng, ParadigmsEng in {
  oper
    Result : Type = Cl ;

    place_N = mkN ("place" | "venue" | "arena") ;
    location_ARG = mkNP the_Det (mkN "{{location}}") ;
    venue_N = mkN "{{venue}}";
    venue_CN mkCN venue_N;
    venue_ARG = mkNP venue_N ;

    -- in the LOCATION
    in_loc = SyntaxEng.mkAdv in_Prep location_ARG;

    -- There is a place in the LOCATION
    place_in_loc = mkCN place_N in_loc;

    -- VENUE in the LOCATION
    in_location       = mkCN venue_NC in_loc;


    -- mkPObj : Prep -> NP -> Str = \prep, np -> lin Cl

    place_phrase = mkCl place_in_loc;

    mkSentence = overload {
              mkSentence : N -> Adv -> VP -> Result = \w1,w2,w3  -> mkCl (mkSC (mkS (mkCl w1))) (mkVP w3 w2) ;
    };

    mkAtLocation : NP -> Adv -> Cl = \v,l -> mkSentence place_N l (mkVP (mkV2 "name") v) ;
}