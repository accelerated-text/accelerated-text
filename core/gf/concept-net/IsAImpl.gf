concrete IsAImpl of IsA = open IsAEng, ParadigmsEng, SyntaxEng, UtilsEng in {
  lincat Message = S;
         SubjectLex, AttributeLex = N;
         SubjectModifierLex1, SubjectModifierLex2, AttributeModifierLex = A;
         SubjectModifier1, SubjectModifier2, SubjectModifier, AttributeModifier = AP;
         Subject, Attribute = NP;

  lin
    IsASentence subject attribute = isA_S subject attribute;

    ModifiedSubject subjectModifier subjectData =
      mkAMod a_Det subjectModifier subjectData;

    ModifiedAttribute attributeModifier attributeData =
      mkAMod a_Det attributeModifier attributeData;

    --
    -- Lexicon
    --

    SimpleSubject subjectData = mkNP subjectData;
    SimpleAttribute attributeData = mkNP attributeData;
    SimpleSubjectModifier1 a = mkAP a;
    SimpleSubjectModifier2 a = mkAP a;
    SimpleAttributeModifier a = mkAP a;
    VeryModifiedSubject mod1 mod2 = combineMods mod1 mod2;

    SubjectData = mkN "T1000";
    AttributeData = mkN "kettle";

    SubjectModData1 = mkA "fearsome";
    SubjectModData2 = mkA "powerful";
    AttributeModData = mkA "red";
}
