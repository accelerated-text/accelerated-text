resource HasAEng = open ParadigmsEng, SyntaxEng, UtilsEng, BaseDictionaryEng in {

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
            with_Prep
            (mkNP a_Det object))) ;
}
