abstract ConceptNet = {
    flags startcat = Message ;
    cat
      Message ; Location ; Venue ; IsA;
    fun
      AtLocation : Venue -> Location -> Message ;
      VenueArg : Venue ;
      LocationArg : Location ;
      ThereIs : IsA ;
}