resource CapableOfEng = open ParadigmsEng, SyntaxEng in {

  oper -- Kettle boils water

    capableOf : N -> V2 -> N -> S =
      \subject,verb,object ->
        (mkS (mkCl (mkNP subject) verb (mkNP object)));

}