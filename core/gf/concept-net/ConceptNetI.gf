incomplete concrete ConceptNetI of ConceptNet = open Syntax, LexConceptNet in {
  lincat
    Message = Text ;
    Venue = NP ;
    Location = NP ;

  lin
    AtLocation v l = mkSentence (mkCl place_N) (mkAdv in_Prep l) (mkNP v named_V2);
    VenueArg = venue_ARG ;
    LocationArg = location_ARG ;
}