resource BaseDictionaryLav = open ResLav, ParadigmsLav, SyntaxLav, UtilsLav in {
  oper

    suitable : A2 = mkA2 (mkA ("piemērots")) for_Prep;

    t1000 : CN = mkCN (mkN "t1000" feminine D5);
    kitchen : CN = mkCN (mkN "virtuve" feminine D5);
    toaster : CN = mkCN (mkN "tosteris" feminine D5);
    lowPower : AP = mkAP (mkA "zema jauda");
    avgSize : AP = mkAP (mkA "vidējais lielums");
    standard : AP = mkAP (mkA "standarta");

    toasterWithMods : CN = mkCN (combineMods lowPower avgSize) toaster;
    kitchenWithMods : CN = mkCN standard kitchen;
}
