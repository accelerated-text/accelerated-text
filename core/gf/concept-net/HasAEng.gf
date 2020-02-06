resource HasAEng = open ParadigmsEng, SyntaxEng, UtilsEng in {

  oper -- A car has an engine

    hasA_S : CN -> CN -> S =
      \subject,object ->
        (mkS
          (mkCl (mkNP a_Det subject)
                have_V2
                (mkNP a_Det object)));

}
