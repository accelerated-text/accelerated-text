resource HasAEng = open ParadigmsEng, SyntaxEng, UtilsEng in {

  oper XhasY : NP -> NP -> S = \x, y -> mkS (mkCl x have_V2 y);

  oper
    hasA_S = overload {
      -- A car has an engine
      hasA_S : N -> N -> S = \subject, object ->
        XhasY (mkNP a_Det subject) (mkNP a_Det object);

      -- A red car has a new engine
      hasA_S : A -> N -> A -> N -> S = \smod, subject, omod, object ->
        XhasY (mkAMod a_Det smod subject) (mkAMod a_Det omod object);

      hasA_S : NP -> NP -> S = \subject, object -> XhasY subject object;
    };
}
