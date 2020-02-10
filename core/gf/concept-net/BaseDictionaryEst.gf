resource BaseDictionaryEst = open ResEst, ParadigmsEst, SyntaxEst, UtilsEst in {
  oper

    suitable : A2 = mkA2 (mkA ("sobivad")) for_Prep;

    t1000 : CN = mkCN (mkN "t1000" );
    kitchen : CN = mkCN (mkN "köök" );
    toaster : CN = mkCN (mkN "röster" );
    water : CN = mkCN (mkN "vesi");
    lowPower : AP = mkAP (mkA "väike võimsus");
    avgSize : AP = mkAP (mkA "keskmine suurus");
    standard : AP = mkAP (mkA "standard");
    cleaning : CN = mkCN (mkN "koristamine");

    toasterWithMods : CN = mkCN (combineMods lowPower avgSize) toaster;
    kitchenWithMods : CN = mkCN standard kitchen;
}
