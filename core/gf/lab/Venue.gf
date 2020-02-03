abstract Venue = {
  flags startcat = DocumentPlan01;
  cat
    -- document
    DocumentPlan01; Segment02;
    -- AMRs
    AtLocation; HasProperty;
    -- Roles
    VenueRole; LocationRole; PropertyNameRole; PropertyPolarityRole; PropertyPredetRole;
    -- Parts
    VenueRoleSubj; HasPropertyCompl; LocationRoleObj;

  fun
    Doc : Segment02 -> DocumentPlan01;
    Seg : AtLocation -> HasProperty -> Segment02;

    AtLocation_Complete :
      VenueRoleSubj -> LocationRoleObj ->  AtLocation;

    HasProperty_Complete :
      VenueRoleSubj -> PropertyNameRole -> PropertyPolarityRole -> HasProperty;

    HasProperty_Complement :
      PropertyNameRole -> PropertyPolarityRole -> HasPropertyCompl;

    VenueRole_Subject1 :
      VenueRole -> VenueRoleSubj;

    VenueRole_Subject2 :
      VenueRole -> HasPropertyCompl -> VenueRoleSubj;

    LocationRole_Object :
      LocationRole -> LocationRoleObj;

    VenueData : VenueRole;
    LocationData : LocationRole;
    PropertyNameData : PropertyNameRole;
    PropertyPolarityData : PropertyPolarityRole;
    PropertyPredetData : PropertyPredetRole;
}
