resource HasPropertyEngImpl = open SyntaxEng, ParadigmsEng, UtilsEng in {

  oper SS : Type = {s : Str} ;

  oper 

    hasProperty : N -> A -> Pol ->  SS = \object, propertyName, polarity ->
                (mkS presentSimTemp polarity (mkCl (mkNP object) propertyName));

    hasProperty_A : A -> Pol -> A = \propertyName, polarity -> propertyName;

}
