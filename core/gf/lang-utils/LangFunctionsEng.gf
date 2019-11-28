concrete LangFunctionsEng of LangFunctions = ResEng ** open CatEng in {
  lincat
    Wrap = {s : Str};
  lin
    Wrapper x = { s = x.s };
  oper
    Result : Type = { s : Str };
    
    mkCopulaWTense : Wrap -> {s : Str} -> Number -> VForm -> Result = \s1,s2,n,t -> {
        s = s1.s ++ (copula n t).s ++ s2.s;
    };
    
    mkCopula : Wrap -> {s : Str} -> Number -> Result = \s1,s2,n -> {
        s = (mkCopulaWTense s1 s2 n VPres).s
    };
    

    copulaWTense : Number -> VForm -> Result = \n,t -> {
                  s = case t of {
                    VPres => case n of { Sg => "is"; Pl => "are" };
                    -- Don't know why, but `VPast` is missing
                    VPast => case n of { Sg => "was"; Pl => "were"};
                    -- VPPart should be `been`
                    VPPart => case n of { Sg => "was"; Pl => "were"};
                    VInf => "be";
                    VPresPart => "being"
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