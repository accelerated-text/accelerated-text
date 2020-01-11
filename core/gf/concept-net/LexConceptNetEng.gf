instance LexConceptNetEng of LexConceptNet = open SyntaxEng, ParadigmsEng in {
  oper

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
    place_in_location = mkCl (mkCN (mkCN place_N in_loc) venue_NP);
    -- In the LOCATION there is a place
    place_ven_Cl = mkCl (mkCN place_N venue_NP); 
    in_location_place = mkS in_loc (mkS present_simultaneous_temp positivePol place_ven_Cl);

    -- VENUE in the LOCATION
    venue_in_location = mkCl (mkCN venue_CN in_loc);

    x1 = mkUtt place_in_location;
    x2 = mkUtt in_location_place;
    x3 = mkUtt venue_in_location;

    atLocation = x1 | x2 | x3 ;
}