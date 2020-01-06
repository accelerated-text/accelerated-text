instance LexConceptNetEng of LexConceptNet = open SyntaxEng, ParadigmsEng in {
  oper
    named_V2 = mkV2 "named" ;
    place_N = mkN ("place" | "venue") ;
    venue_ARG = mkNP (mkN "{{venue}}") ;
    location_ARG = mkNP the_Det (mkN "{{location}}") ;
}