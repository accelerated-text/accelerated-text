concrete LangFunctionsEng of LangFunctions = ResEng ** open CatEng in {
  lincat
    Wrap = {s : Str};
  lin
    Wrapper x = { s = x.s };
  oper
    Result : Type = { s : Str };
    mkCopula : Wrap -> {s : Str} -> Number -> Result = \s1,s2,n -> {
        s = s1.s ++ (copula n).s ++ s2.s;
    };

    copulaWTense : Number -> VForm -> Result = \n,t -> {
                  s = case t of {
                    VPres => case n of { Sg => "is"; Pl => "are" };
                    -- Don't know why, but `VPast` is missing
                    -- VPast => case n of { Sg => "was"; Pl => "were"};
                    VPPart => case n of { Sg => "was"; Pl => "were"};
                    VInf => "...";
                    VPresPart => "..."
                    
                  }
    };

    copulaSimple : Number -> Result = \n -> {
                 s = (copulaWTense n VPres).s
    };

    copula = overload {
           copula : Number -> VForm -> Result = copulaWTense;
           copula : Number -> Result = copulaSimple;
    };

    mkPassive : V -> Result = \w -> {
        s = (copula Sg).s ++ (w.s ! VPPart);
    };

    mkPast : V -> Result = \w -> {
        -- Should be:
        -- s = w.s ! VPast;
        -- but VPast is missing
        s = w.s ! VPPart;
    };
}