instance LexConceptNetEng of LexConceptNet = open SyntaxEng, ParadigmsEng in {
  oper
    Result : Type = Cl ;
    named_V2 = mkV2 "name" ;
    place_N = mkN ("place" | "venue") ;
    venue_ARG = mkNP (mkN "{{venue}}") ;
    location_ARG = mkNP the_Det (mkN "{{location}}") ;

    mkSentence = overload {
              mkSentence : N -> Adv -> NP -> Result = \w1,w2,w3  -> mkCl (mkNP w1) (mkNP w3 w2) ;
    };

    mkAtLocation : NP -> Adv -> Cl = \v,l -> mkSentence place_N l (mkNP v named_V2) ;
}