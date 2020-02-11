resource MaintenanceEst = open
  ParadigmsEst, SyntaxEst, BaseDictionaryEst in {

  oper withSomething : CN -> Adv =
         \thing ->
         (SyntaxEst.mkAdv with_Prep (mkNP the_Det thing));

       -- Cleaning is easy with removable filter
  oper
    maintenance : CN -> CN -> S = \subject, object ->
      (mkS
         (mkCl
            (mkNP subject)
            (mkNP (mkCN (mkN "easy") (withSomething object)))));

}
