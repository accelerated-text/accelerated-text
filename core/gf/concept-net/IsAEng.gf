resource IsAEng = open ParadigmsEng, SyntaxEng, UtilsEng in {

  oper

    isA_S : CN -> CN -> Text =
      \subject,attribute ->
        (mkText
          (mkS
            (mkCl (mkNP subject)
                  (mkNP a_Det attribute)))) ;

}
