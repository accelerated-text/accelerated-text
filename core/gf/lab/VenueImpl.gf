incomplete concrete VenueImpl of Venue = open
  AtLocationEngImpl, HasPropertyEngImpl,
  SyntaxEng, ParadigmsEng in {

  lincat
    DocumentPlan01, Segment02 = {s: Str};
    AtLocation, IsA, HasProperty = S;
    ObjectRole, LocationRole, PropertyNameRole = N;
    PropertyPolarityRole = Pol;

    Doc seg = {s = seg.s};
    Seg location isA hasProp = {s =
                                  location.s ++ "."
                                  isA.s ++ "."
                                  hasPorp.s ++ "."};

    AtLocation_Complete objectSubj locationObj = atLocationX objectSubj locationObj;

    IsA_Complete objectSubj = isA objectSubj;

    HasProperty_Complete objectSubj propertyCompl = hasProperty objectSubj propertyCompl;

    HasProperty_Complement :
      PropertyNameRole -> PropertyPolarityRole -> HasPropertyCompl;

    ObjectRole_Subject :
      ObjectRole -> ObjectRoleSubj;

    ObjectRole_Subject :
      ObjectRole -> HasPropertyCompl -> ObjectRoleSubj;

    LocationRole_Object :
      LocationRole -> LocationRoleObj;

    ObjectData = mkN "Aromi";
    LocationData = mkN "place";
    PropertyNameData = mkN "family-friendly";
    PropertyPolarityData = negativePol;
}
