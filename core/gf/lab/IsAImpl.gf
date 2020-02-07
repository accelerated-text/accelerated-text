--# -path=../concept-net
concrete IsAImpl of IsA = open IsAEng, ParadigmsEng, SyntaxEng, UtilsEng in {
  lincat Message = S;
         Subject, Attribute = NP;

  lin
    IsASentence subject attribute = isA_S subject attribute;

    SubjectData =
      mkAMod the_Det (combineMods (mkAP (mkA "fearsome")) (mkAP (mkA "powerful"))) (mkN "T1000");

    AttributeData = mkAMod a_Det (mkAP (mkA "red")) (mkN "kettle");
}
