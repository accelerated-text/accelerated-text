--# -path=../concept-net
concrete MadeOfImpl of MadeOf = open MadeOfEng, ParadigmsEng, SyntaxEng, UtilsEng in {
  lincat Message = S;
         Subject, Object = N;
         SubjectLex, ObjectLex = N;

  lin
    MadeOfSentence subject object = madeOf subject object;

    SimpleSubject subjectLex = subjectLex;
    SimpleObject objectLex = objectLex;

    SubjectData = mkN "kettle";
    ObjectData = mkN "glass";

}
