resource BaseDictionaryEng = open ParadigmsEng, SyntaxEng, UtilsEng in {
  oper

  suitable : A2 = mkA2 (mkA ("suitable" | "good")) for_Prep;
  allows: V2 = mkV2 (mkV ("allow" | "enable")) for_Prep;
  features: V2 = mkV2 (mkV "feature");
  includes: V2 = mkV2 (mkV ("include" | "contain" ));

  this: CN = mkCN (mkN "this");

  t1000 : CN = mkCN (mkN "t1000");
  kitchen : CN = mkCN (mkN "kitchen");
  toaster : CN = mkCN (mkN "toaster");
  water : CN = mkCN (mkN "water");
  cleaning : CN = mkCN (mkN "cleaning");
  safe_operation : CN = mkCN (mkN "safe operation");
  auto_switch : CN =  mkCN (mkN "auto switch-off");
  kettle : CN =  mkCN (mkN "kettle");
  fridge : CN =  mkCN (mkN "fridge");
  door: CN =  mkCN (mkN "door");
  steel : CN =  mkCN (mkN "steel");
  wood : CN =  mkCN (mkN "wood");
  removable_filter : CN = mkCN (mkN "removable filter");
  package : CN = mkCN (mkN "package");
  interior_use : CN = mkCN (mkN "interior use");

  easy_N : N = mkN "easy";

  low_power : A = (mkA "low power");
  average_size: A = (mkA "average size");
  standard : A = (mkA "standard");
  fast : A = (mkA "fast");
  small : A = (mkA "small");
  regular : A = (mkA "regular");

  make : V2 = mkV2 (mkV "make" "made" "made");

  of_Prep : Prep = mkPrep "of";
  with_Prep : Prep = mkPrep "with";

  -- for dev
  -- toasterWithMods : CN = mkCN (combineMods low_power average_size) toaster;
  -- kitchenWithMods : CN = mkCN standard kitchen;
}
