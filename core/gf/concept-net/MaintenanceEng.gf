resource MaintenanceEng = open
  ParadigmsEng, SyntaxEng, BaseDictionaryEng in {

  oper withSomething : CN -> Adv =
         \thing ->
         (SyntaxEng.mkAdv with_Prep (mkNP the_Det thing));

  -- Cleaning is easy with removable filter
  oper
    maintenance : CN -> CN -> S = \subject, object ->
      (mkS
         (mkCl
            (mkNP subject)
            (mkNP (mkCN easy_N (withSomething object)))));

}
