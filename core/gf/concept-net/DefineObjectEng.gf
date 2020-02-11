resource DefineObjectEng = open
  ParadigmsEng, SyntaxEng, UtilsEng, BaseDictionaryEng in {
  -- for X
  oper forPrep : CN -> Adv = \x -> (SyntaxEng.mkAdv for_Prep (mkNP x));


  oper forObject : A2 -> CN -> AP = \for, object ->
         mkAP for (mkNP a_Det object);

  -- T1000 is an average size, low power toaster suitable for the standard kitchen
  oper
    characterization : CN -> CN -> CN -> Text =
      \subject,attribute,amod ->
      (mkText
        (mkS
          (mkCl
             (mkNP a_Det subject)
             (mkNP
                the_Det
                (mkCN
                   attribute
                   (forObject suitable amod))))));
}
