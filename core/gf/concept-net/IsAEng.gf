resource IsAEng = open ParadigmsEng, SyntaxEng in {

  oper
    isA_S = overload {
      -- T1000 is a kettle
      isA_S : N -> N -> S = \subject, attribute ->
        mkS (mkCl (mkNP subject) (mkNP a_Det attribute));

      -- T1000 is a red kettle
      isA_S : N -> A -> N -> S = \subject, amod, attribute ->
        mkS (mkCl
               (mkNP subject)
               (mkNP a_Det (mkCN amod attribute)));
      };
}
