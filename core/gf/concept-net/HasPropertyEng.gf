resource HasPropertyEng = open SyntaxEng, ParadigmsEng, UtilsEng in {

  oper -- hasProperty

    hasProperty : N -> A -> Pol ->  S =
      \object, propertyName, polarity ->
        (mkS presentSimTemp polarity (mkCl (mkNP object) propertyName)) ;

}