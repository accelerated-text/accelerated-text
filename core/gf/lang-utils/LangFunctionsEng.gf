concrete LangFunctionsEng of LangFunctions = open ResEng in {
  lincat
    Wrap = {s : Str};
  lin
    Wrapper x = { s = x.s };
  oper
    Result : Type = { s : Str };
    mkCopula : Wrap -> {s : Str} -> Number -> Result = \s1,s2,n -> {
        s = s1.s ++ (copula n).s ++ s2.s;
    };

    copula : Number -> Result = \n -> { s = case n of { Sg => "is"; Pl => "are" } };
}