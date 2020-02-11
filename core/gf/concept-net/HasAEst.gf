resource HasAEst = open ParadigmsEst, SyntaxEst, UtilsEst, BaseDictionaryEst in {

  oper -- A car has an engine

    hasA_S : CN -> CN -> Text =
      \subject,object ->
      (mkText
        (mkS
          (mkCl (mkNP the_Det subject)
             have_V2
             (mkNP a_Det object))));
}
