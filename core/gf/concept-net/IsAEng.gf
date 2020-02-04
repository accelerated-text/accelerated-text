resource IsAEng = open ParadigmsEng, SyntaxEng, UtilsEng in {

  oper XisY : NP -> NP -> S = \x, y-> mkS (mkCl x y);

  oper
    isA_S = overload {
      -- T1000 is a kettle
      isA_S : N -> N -> S = \subject, attribute ->
        XisY (mkNP subject) (mkNP a_Det attribute) ;

      -- T1000 is a red kettle
      isA_S : N -> A -> N -> S = \subject, amod, attribute ->
        XisY (mkNP subject) (mkAMod a_Det amod attribute);

      -- A fearsome T1000 is a red kettle
      isA_S : A -> N -> A -> N -> S = \smod, subject, amod, attribute ->
        XisY (mkAMod a_Det smod subject) (mkAMod a_Det amod attribute);

      isA_S : NP -> NP -> S = \subject, attribute -> XisY subject attribute;
    };
}
