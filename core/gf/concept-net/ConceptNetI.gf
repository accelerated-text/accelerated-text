incomplete concrete ConceptNetI of ConceptNet = open Syntax, LexConceptNet in {
  lincat
    Message = Utt ;
    Venue = Str ;
    Location = Str ;

  lin
    AtLocation v l = atLocation ;
    VenueArg = venue_ARG ;
    LocationArg = location_ARG ;
}