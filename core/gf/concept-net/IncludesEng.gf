resource IncludesEng = open ParadigmsEng, SyntaxEng, UtilsEng, BaseDictionaryEng in {

  oper wrapInText : Cl -> Text = \cl -> (mkText (mkS cl));

  oper
    -- The package includes X.
    included : CN -> Text =
      \subject ->
      (wrapInText
         (mkCl
            (mkNP the_Det package)
            includes
            (mkNP a_Det subject)));
}
