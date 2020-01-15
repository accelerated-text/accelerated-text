instance LexConceptNetEng of LexConceptNet = open SyntaxEng, ParadigmsEng, UtilsEng in {
  oper

    objectRef = mkN "[VENUE_NAME_ARG]";
    locationData = mkN "[LOCATION_ARG]";
    locationDictionary = mkN ("place" | "venue" | "arena");

    -- There is a place in the LOCATION
    place_in_location =
    mkUtt (mkThereIsAThing (mkCN locationDictionary (mkInAdv locationData))
                           objectRef);

    -- In the LOCATION there is a place
    in_location_place =
    mkUtt (mkS (mkInAdv locationData)
               (mkS mkPresentSimTemp positivePol
                                     (mkThereIsAThing locationDictionary objectRef)));

    -- VENUE in the LOCATION
    venue_in_location =
    mkUtt (mkThereIsAThing objectRef
                           (mkInAdv locationData));

    atLocation = place_in_location | in_location_place | venue_in_location;
}