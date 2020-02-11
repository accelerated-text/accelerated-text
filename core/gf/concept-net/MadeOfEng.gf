resource MadeOfEng = open ParadigmsEng, ConstructorsEng, SyntaxEng, BaseDictionaryEng in {

  oper -- refrigerator is made of steel

    madeOf : CN -> CN -> S =
      \subject,object ->
        (mkS
          (mkCl
            (mkNP subject)
            (mkVP
              (passiveVP make)
              (ConstructorsEng.mkAdv of_Prep (mkNP object)))));
}
