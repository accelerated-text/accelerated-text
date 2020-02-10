resource BaseDictionaryEng = open ParadigmsEng, SyntaxEng, UtilsEng in {
  oper

  suitable : A2 = mkA2 (mkA ("suitable" | "good")) for_Prep;

  t1000 : CN = mkCN (mkN "t1000");
  kitchen : CN = mkCN (mkN "kitchen");
  toaster : CN = mkCN (mkN "toaster");
  lowPower : AP = mkAP (mkA "low power");
  avgSize : AP = mkAP (mkA "average size");
  standard : AP = mkAP (mkA "standard");

  toasterWithMods : CN = mkCN (combineMods lowPower avgSize) toaster;
  kitchenWithMods : CN = mkCN standard kitchen;
}
