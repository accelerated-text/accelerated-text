resource HasPropertyEngImpl = open SyntaxEng, ParadigmsEng, UtilsEng in {

  oper SS : Type = {s : Str} ;

  oper 

    hasProperty : N -> A -> Pol ->  S = \object, propertyName, polarity ->
                (mkS presentSimTemp polarity (mkCl (mkNP object) propertyName));

    hasProperty_A : A -> Pol -> A = \propertyName, polarity -> propertyName;

    hasProp_Mod2 : N -> AP -> CN =
      \subject, modifier ->
      (mkCN modifier subject);

    hasProp_Mod1 : N -> CN = \subject -> mkCN subject;

    hasProp_Compl : A -> Pol -> AP = \name, pol -> mkAP name;

}
