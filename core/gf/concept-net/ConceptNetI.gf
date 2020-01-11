incomplete concrete ConceptNetI of ConceptNet = open Syntax, LexConceptNet in {
  lincat
    Message = Utt ;
    Venue = NP ;
    Location = NP ;

  lin
    AtLocation v l = atLocation ;
    VenueArg = venue_ARG ;
    LocationArg = location_ARG ;
}