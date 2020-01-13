instance LexConceptNetEng of LexConceptNet = open SyntaxEng, ParadigmsEng in {
  oper

    venue_name_ARG = "[VENUE_NAME_ARG]";
    location_ARG = "[LOCATION_ARG]";

    place_N = mkN ("place" | "venue" | "arena") ;

    venue_N = mkN venue_name_ARG;
    venue_CN = mkCN venue_N;
    venue_NP = mkNP venue_N;
    location_NP = mkNP the_Det (mkN location_ARG) ;

    present_simultaneous_temp = mkTemp presentTense simultaneousAnt;

    -- in the LOCATION
    in_loc = SyntaxEng.mkAdv in_Prep location_NP;

    -- There is a place in the LOCATION
    place_in_location = mkCl (mkCN (mkCN place_N in_loc) venue_NP);
    -- In the LOCATION there is a place
    place_ven_Cl = mkCl (mkCN place_N venue_NP); 
    in_location_place = mkS in_loc (mkS present_simultaneous_temp positivePol place_ven_Cl);

    -- VENUE in the LOCATION
    venue_in_location = mkCl (mkCN venue_CN in_loc);

    v1 = mkUtt place_in_location;
    v2 = mkUtt in_location_place;
    v3 = mkUtt venue_in_location;

    atLocation = v1 | v2 | v3 ;
}