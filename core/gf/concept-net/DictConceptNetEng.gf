instance DictConceptNetEng of DictConceptNet = open SyntaxEng, ParadigmsEng in {
  oper objectRef = "[VENUE_NAME_ARG]";
  oper locationData = "[LOCATION_ARG]";
  oper locationDictionary = "place" | "venue" | "arena";
}