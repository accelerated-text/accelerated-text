resource HasPropertyEng = open SyntaxEng, ParadigmsEng, UtilsEng in {

  oper -- hasProperty

    hasProperty : CN -> A -> Pol -> Text =
      \object,propertyName,polarity ->
        (mkText
          (mkS
            (mkTemp presentTense simultaneousAnt)
            polarity
            (mkCl (mkNP object) propertyName))) ;

}
