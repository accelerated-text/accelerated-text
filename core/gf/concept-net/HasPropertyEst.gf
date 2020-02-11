resource HasPropertyEst = open SyntaxEst, ParadigmsEst, UtilsEst in {

  oper -- hasProperty

    hasProperty : CN -> A -> Pol -> Text =
      \object,propertyName,polarity ->
        (mkText
          (mkS
            (mkTemp presentTense simultaneousAnt)
            polarity
            (mkCl (mkNP object) propertyName))) ;

}