resource CapableOfEng = open ParadigmsEng, SyntaxEng in {

  oper -- Kettle boils water

    capableOf = overload {
      capableOf : CN -> V2 -> CN -> Text =
        \subject,verb,object ->
        (mkText
          (mkS
            (mkCl (mkNP subject)
               verb
               (mkNP object)))) ;

      capableOf : NP -> V2 -> CN -> Text =
        \subject,verb,object ->
        (mkText
          (mkS
            (mkCl subject
               verb
               (mkNP object)))) ;

      };

}
