--# -path=../concept-net:../lang-utils
concrete VenueImpl of Venue = open
  AtLocationEngImpl, HasPropertyEngImpl, UtilsEng,
  SyntaxEng, ParadigmsEng in {

  lincat
    DocumentPlan01, Segment02 = {s: Str};
    AtLocation, HasProperty = S;
    VenueRole, LocationRole = N;
    PropertyNameRole = A;
    PropertyPolarityRole = Pol;

    HasPropertyAcl = AP;
    LocationRoleObj = Adv;
    VenueRoleSubj = CN;
  lin
    Doc seg = {s = seg.s};
    -- Aromi is located in the city. Aromi is FF.
    Seg_Sentences location hasProp = {s = location.s ++ "." ++ hasProp.s ++ "."};
    -- Family-friendly Aromi is located in the city.
    Seg_Phrases venue location = subjectCopulaObject venue location;

    AtLocation_Complete venueSubj locationObj = atLocationX venueSubj locationObj;

    HasProperty_Complete venueSubj propName polarity =
      hasProperty venueSubj propName polarity;

    HasProperty_Acl propertyName propertyPolarity =
      hasProp_Compl propertyName propertyPolarity;

    VenueRole_Subject1 venueRole = hasProp_Mod1 venueRole;

    VenueRole_Subject2 venueRole property = hasProp_Mod2 venueRole property;

    LocationRole_Object location = atLocationY location;

    VenueData = mkN "Aromi";
    LocationData = mkN "riverside";
    PropertyNameData = mkA  "family-friendly";
    PropertyPolarityData = negativePol;
    PropertyPredetData = not_Predet;
}
