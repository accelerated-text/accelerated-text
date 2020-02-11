resource MadeOfEng = open ParadigmsEng, ConstructorsEng, SyntaxEng, BaseDictionaryEng in {

  oper -- refrigerator is made of steel

    madeOf : CN -> CN -> Text =
      \subject,object ->
        (mkText
          (mkS
            (mkCl
              (mkNP subject)
              (mkVP
                (passiveVP make)
                (ConstructorsEng.mkAdv of_Prep (mkNP object))))));
}
