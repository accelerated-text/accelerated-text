instance LexConceptNetEng of LexConceptNet = open SyntaxEng, ParadigmsEng in {
  oper
    Result : Type = Cl ;

    place_N = mkN ("place" | "venue" | "arena") ;
    location_ARG = mkNP the_Det (mkN "{{location}}") ;
    venue_ARG = mkNP (mkN "{{venue}}") ;

    -- there is X
    place_in_location = mkCN   (mkN2 place_N (mkPrep "in")) location_ARG;
    -- in the {location} there is
    in_location       = mkPrep (mkN2 place_N (mkPrep "in")) location_ARG ;

    place_phrase = mkCl place_in_location;

    mkSentence = overload {
              mkSentence : N -> Adv -> VP -> Result = \w1,w2,w3  -> mkCl (mkSC (mkS (mkCl w1))) (mkVP w3 w2) ;
    };

    mkPrep

    mkAtLocation : NP -> Adv -> Cl = \v,l -> mkSentence place_N l (mkVP (mkV2 "name") v) ;
}