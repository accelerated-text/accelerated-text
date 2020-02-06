resource HasPropertyEng = open SyntaxEng, ParadigmsEng, UtilsEng in {

  oper -- hasProperty

    hasProperty : CN -> A -> Pol ->  S =
      \object,propertyName,polarity ->
        (mkS
          (mkTemp presentTense simultaneousAnt)
          polarity
          (mkCl (mkNP object) propertyName)) ;

}