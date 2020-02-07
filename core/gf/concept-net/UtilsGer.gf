resource UtilsGer = open SyntaxGer in {
  oper
    mkAMod : Det -> A -> N -> NP = \det, mod, noun ->
      (mkNP det (mkCN mod noun));
}
