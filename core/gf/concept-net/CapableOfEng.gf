resource CapableOfEng = open ParadigmsEng, SyntaxEng in {

  oper -- Kettle boils water

    capableOf : CN -> V2 -> CN -> S =
      \subject,verb,object ->
        (mkS
          (mkCl (mkNP subject)
                verb
                (mkNP object))) ;

}
