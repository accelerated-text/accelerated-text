abstract Venue = {
  flags startcat = DocumentPlan01;
  cat
    -- document
    DocumentPlan01; Segment02;
    -- AMRs
    AtLocation; IsA; HasProperty;
    -- Roles
    ObjectRole; LocationRole; PropertyNameRole; PropertyPolarityRole;

  fun
    Doc : Segment02 -> DocumentPlan01;
    Seg : AtLocation -> IsA -> HasProperty -> Segment02;

    AtLocation_Complete :
      ObjectRoleSubj -> LocationRoleObj ->  AtLocation;

    IsA_Complete :
      ObjectRoleSubj -> IsA;

    HasProperty_Complete :
      ObjectRoleSubj -> HasPropertyCompl -> HasProperty;

    HasProperty_Complement :
      PropertyNameRole -> PropertyPolarityRole -> HasPropertyCompl;

    ObjectRole_Subject :
      ObjectRole -> ObjectRoleSubj;

    ObjectRole_Subject :
      ObjectRole -> HasPropertyCompl -> ObjectRoleSubj;

    LocationRole_Object :
      LocationRole -> LocationRoleObj;

    ObjectData : ObjectRole;
    LocationData : LocationRole;
    PropertyNameData : PropertyNameRole;
    PropertyPolarityData : PropertyPolarityRole;
}
