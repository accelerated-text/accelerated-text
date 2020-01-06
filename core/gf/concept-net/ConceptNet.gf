abstract ConceptNet = {
    flags startcat = Message ;
    cat
      Message ; Venue ; Location ;
    fun
      AtLocation : Venue -> Location -> Message ;
}