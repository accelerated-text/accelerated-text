incomplete concrete ConceptNetI of ConceptNet = open Syntax, LexConceptNet in {
  lincat Message = Utt ;
  lincat ObjectRef = N;
  lincat LocationData = N;
  lincat LocationDictionary = N;

  lin AtLocation obj loc = atLocation;
  lin VenueRef = objectRef;
  lin GeoLoc = locationData;
  lin PlaceDict = locationDictionary;
}