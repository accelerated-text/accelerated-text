--# -path=../concept-net
concrete MadeOfImpl of MadeOf = open MadeOfEng, ParadigmsEng, SyntaxEng, UtilsEng in {
  lincat Message = S;
         Subject, Object = N;

  lin
    MadeOfSentence subject object = madeOf subject object;

    SubjectData = mkN "kettle";
    ObjectData = mkN "glass";
}
