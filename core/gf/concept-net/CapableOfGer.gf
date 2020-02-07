resource CapableOfGer = open ParadigmsGer, SyntaxGer in {

  oper -- Wasserkocher kocht Wasser
    capableOf : N -> V2 -> N -> S =
      \subject,verb,object ->
      (mkS (mkCl (mkNP subject) verb (mkNP object)));
}
