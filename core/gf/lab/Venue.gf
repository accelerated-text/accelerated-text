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
    VenueRoleSubj; LocationRoleObj;
    HasPropertyAcl;

  fun
    Doc : Segment02 -> DocumentPlan01;
    Seg_Sentences : AtLocation -> HasProperty -> Segment02;
    Seg_Phrases : VenueRoleSubj -> LocationRoleObj -> Segment02;

    Seg : AtLocation -> HasProperty -> Segment02;

    AtLocation_Complete :
      VenueRoleSubj -> LocationRoleObj ->  AtLocation;

    HasProperty_Complete :
      VenueRoleSubj -> PropertyNameRole -> PropertyPolarityRole -> HasProperty;

    HasProperty_Acl:
      PropertyNameRole -> PropertyPolarityRole -> HasPropertyAcl;

    VenueRole_Subject1 :
      VenueRole -> VenueRoleSubj;

    VenueRole_Subject2 :
      VenueRole -> HasPropertyAcl -> VenueRoleSubj;

    LocationRole_Object :
      LocationRole -> LocationRoleObj;

    VenueData : VenueRole;
    LocationData : LocationRole;
    PropertyNameData : PropertyNameRole;
    PropertyPolarityData : PropertyPolarityRole;
    PropertyPredetData : PropertyPredetRole;
}
