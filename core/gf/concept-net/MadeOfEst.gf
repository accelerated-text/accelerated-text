resource MadeOfEst = open ParadigmsEst, ConstructorsEst, SyntaxEst, BaseDictionaryEst in {

  oper -- refrigerator is made of steel

    madeOf : CN -> CN -> Text =
      \subject,object ->
      (mkText
        (mkS
          (mkCl
             (mkNP subject)
             (mkVP
                (passiveVP make)
                (ConstructorsEst.mkAdv from_Prep (mkNP object))))));
}
