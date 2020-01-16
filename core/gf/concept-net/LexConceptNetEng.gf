instance LexConceptNetEng of LexConceptNet = open SyntaxEng, ParadigmsEng, UtilsEng, DictConceptNetEng in {
  oper

    objectRef_N = mkN objectRef;
    locationData_N = mkN objectRef;
    locationDictionary_N = mkN locationDictionary;

    -- There is a place in the LOCATION
    place_in_location =
    mkUtt (mkThereIsAThing (mkCN locationDictionary_N (mkInAdv locationData_N))
                           objectRef_N);

    -- In the LOCATION there is a place
    in_location_place =
    mkUtt (mkS (mkInAdv locationData_N)
               (mkS mkPresentSimTemp positivePol
                                     (mkThereIsAThing locationDictionary_N objectRef_N)));

    -- VENUE in the LOCATION
    venue_in_location =
    mkUtt (mkThereIsAThing objectRef_N
                           (mkInAdv locationData_N));

    atLocation = place_in_location | in_location_place | venue_in_location;
}