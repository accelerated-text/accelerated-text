resource ConceptNetEng = open SyntaxEng, ParadigmsEng, UtilsEng, (R=ResEng) in {

  oper SS : Type = {s : Str} ;

  oper -- hasProperty

    hasProperty : N -> A -> Pol ->  SS = \object, propertyName, polarity ->
                (mkS presentSimTemp polarity (mkCl (mkNP object) propertyName));

  oper -- capableOf 'Something that A can typically do is B.'

    capableOfImpl : V2 -> NP -> VP =
                    \action, result -> (mkVP action result);

    capableOf = overload {
      capableOf : V2 -> N -> A -> SS = 
                  \action, result, modifier ->
                  (mkS presentSimTemp positivePol
                       (mkCl (capableOfImpl action (mkNP (mkCN modifier result)))));

      capableOf : V2 -> NP -> SS = 
                  \action, result ->
                  (mkS presentSimTemp positivePol
                       (mkCl (capableOfImpl action result))) ;
    };
}