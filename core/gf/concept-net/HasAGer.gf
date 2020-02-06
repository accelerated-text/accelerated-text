resource HasAGer = open ParadigmsGer, SyntaxGer, UtilsGer in {

  oper
    XhasY : NP -> NP -> S = \x, y -> mkS (mkCl x have_V2 y);

    hasA_S = overload {
      -- Ein Auto hat einen Motor
      hasA_S : N -> N -> S = \subject, object ->
        XhasY (mkNP a_Det subject) (mkNP a_Det object);

      -- Ein rotes Auto hat einen neuen Motor
      hasA_S : A -> N -> A -> N -> S = \smod, subject, omod, object ->
       XhasY (mkAMod a_Det smod subject) (mkAMod a_Det omod object);
      };
}
