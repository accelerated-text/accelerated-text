resource HasFeatures = open
  ParadigmsEng, SyntaxEng, UtilsEng, (R=ResEng), BaseDictionaryEng in {

  oper itHasX : CN -> CN -> S = \parent, hasX -> (mkS (mkCl (mkNP parent) features (mkNP hasX)));

  oper thisAllowsFor : CN -> S = \benefit ->
         (mkS (mkCl (mkNP this) allows (mkNP the_Det benefit)));

  -- The kettle features auto switch-off, this allows for the safe operation
  oper
    allowsFor : CN -> CN -> CN -> S =
      \owner, subject, object ->
      (mkS
         (mkConj ",")
         (itHasX owner subject)
         (thisAllowsFor object));
}
