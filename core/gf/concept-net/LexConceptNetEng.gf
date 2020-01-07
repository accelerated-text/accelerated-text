instance LexConceptNetEng of LexConceptNet = open SyntaxEng, ParadigmsEng in {
  oper
    Result : Type = Text;
    named_V2 = mkV2 "named" ;
    place_N = mkN ("place" | "venue") ;
    venue_ARG = mkNP (mkN "{{venue}}") ;
    location_ARG = mkNP the_Det (mkN "{{location}}") ;

    mkSentence = overload {
              mkSentence : Cl -> Adv -> NP -> Result = \w1,w2,w3  -> mkText (mkText w1) (mkText (mkText (mkUtt w2)) (mkText (mkUtt w3))) ;
    };
}