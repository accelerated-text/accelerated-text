incomplete concrete ConceptNetI of ConceptNet = open Syntax, LexConceptNet in {
  lincat
    Message = Cl ;
    Venue = NP ;
    Location = NP ;

  lin
    AtLocation v l = mkAtLocation v (mkAdv in_Prep l) ;
    VenueArg = venue_ARG ;
    LocationArg = location_ARG ;
}