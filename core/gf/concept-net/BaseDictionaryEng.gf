resource BaseDictionaryEng = open ParadigmsEng, SyntaxEng, UtilsEng in {
  oper

  suitable : A2 = mkA2 (mkA ("suitable" | "good")) for_Prep;
  allows: V2 = mkV2 (mkV ("allow" | "enable")) for_Prep;
  features: V2 = mkV2 (mkV "feature");
  includes: V2 = mkV2 (mkV ("include" | "contain" ));

  this: CN = mkCN (mkN "this");

  easy_N : N = mkN "easy";

  standard : A = (mkA "standard");
  fast : A = (mkA "fast");
  small : A = (mkA "small");
  regular : A = (mkA "regular");

  make : V2 = mkV2 (mkV "make" "made" "made");

  of_Prep : Prep = mkPrep "of";
  with_Prep : Prep = mkPrep "with";
}
