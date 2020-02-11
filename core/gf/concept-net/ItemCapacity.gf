-- This 5000W kettle will boil water fast.
resource ItemCapacity = open
  ParadigmsEng, SyntaxEng, UtilsEng, BaseDictionaryEng in {

  oper

    doSomething: V -> A -> VP = \action, mode ->
      mkVP (mkVP action) (SyntaxEng.mkAdv mode);

    capacity : CN -> V2 -> CN -> S =
      \subject,verb,object ->
      (mkS
         futureTense
         (mkCl
            (mkNP this_Det subject)
            verb
            (mkNP object))) ;
}
