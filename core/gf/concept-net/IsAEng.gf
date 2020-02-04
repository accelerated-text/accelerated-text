resource IsAEng = open ParadigmsEng, SyntaxEng in {

  -- X is Y
  oper XisY : NP -> NP -> S = \x, y-> mkS (mkCl x y);

  oper
    isA_S = overload {
      -- T1000 is a kettle
      isA_S : N -> N -> S = \subject, attribute ->
        XisY (mkNP subject) (mkNP a_Det attribute) ;

      -- T1000 is a red kettle
      isA_S : N -> A -> N -> S = \subject, amod, attribute ->
        XisY (mkNP subject) (mkNP a_Det (mkCN amod attribute));

      isA_S : NP -> NP -> S = \subject, attribute -> XisY subject attribute;
    };
}
