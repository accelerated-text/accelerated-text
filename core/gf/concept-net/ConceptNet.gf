abstract ConceptNet = {
    flags startcat = Message ;
    cat
      Message ; Location ; Venue ;
    fun
      AtLocation : Venue -> Location -> Message ;
      VenueArg : Venue ;
      LocationArg : Location ;
}