resource UtilsEng = open SyntaxEng in {

  -- Takes in a noun and produces 'in the NOUN'
  oper mkInAdv : N -> Adv = \noun -> SyntaxEng.mkAdv in_Prep (mkNP the_Det noun);

  oper presentSimTemp = mkTemp presentTense simultaneousAnt;

  -- Takes in noung for a reference place and a thing in that reference place
  -- produces 'There is X in Y'
  oper mkThereIsAThing = overload {
    mkThereIsAThing : N -> N -> Cl = \descriptionN,thingN -> mkCl (mkCN descriptionN (mkNP thingN));
    mkThereIsAThing : CN -> N -> Cl = \descriptionCN,thingN -> mkCl (mkCN descriptionCN (mkNP thingN));
    mkThereIsAThing : N -> Adv -> Cl = \descriptionN,thingAdv -> mkCl (mkCN (mkCN descriptionN) thingAdv);
  };
}