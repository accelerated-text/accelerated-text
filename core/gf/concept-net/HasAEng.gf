resource HasAEng = open ParadigmsEng, SyntaxEng, UtilsEng in {

  oper -- A car has an engine

    hasA_S : CN -> CN -> S =
      \subject,object ->
        (mkS
          (mkCl (mkNP the_Det subject)
                have_V2
             (mkNP a_Det object)));

    hasA_NP : CN -> CN -> NP =
      \subject, object ->
      (mkNP
         (mkNP a_Det subject)
         (SyntaxEng.mkAdv
            (mkPrep "with")
            (mkNP a_Det object))) ;
}
