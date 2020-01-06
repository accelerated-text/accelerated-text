incomplete concrete ConceptNetI of ConceptNet = open Syntax, LexConceptNet in {
  lincat
    Message = Text ;
    Venue = NP ;
    Location = NP ;

  lin
    AtLocation v l = mkText { s = "There is a place in" ++ l.s ++ "named" ++ n.s }
    Venue = mkNP "{{venue}}"
    Location = mkNP "{{location}}"
}