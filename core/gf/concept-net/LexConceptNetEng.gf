instance LexConceptNetEng of LexConceptNet = open SyntaxEng, ParadigmsEng, UtilsEng, DictConceptNetEng in {
  oper

    -- --
    -- At Location
    -- --

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
               (mkS presentSimTemp positivePol
                                   (mkThereIsAThing locationDictionary_N objectRef_N)));

    -- VENUE in the LOCATION
    venue_in_location =
    mkUtt (mkThereIsAThing objectRef_N
                           (mkInAdv locationData_N));

    atLocation = place_in_location | in_location_place | venue_in_location;

    -- --
    -- Has Property
    -- --

    -- mkCl 	NP -> A -> Cl she is old


    object_Pron = mkN "it";
    propertyValue = positivePol;
    propertyName_A = mkA "[FF]";
    itHas = mkS presentSimTemp propertyValue (mkCl (mkNP object_Pron) propertyName_A);

    hasProperty = mkUtt itHas;
}