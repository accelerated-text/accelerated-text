resource BaseDictionaryEst = open ResEst, ParadigmsEst, SyntaxEst, UtilsEst in {
  oper

    suitable : A2 = mkA2 (mkA ("sobivad")) for_Prep;
    allows: V2 = mkV2 (mkV "lube") for_Prep;
    features: V2 = mkV2 (mkV "näojooned");

    this: CN = mkCN (mkN "see");

    t1000 : CN = mkCN (mkN "t1000" );
    kitchen : CN = mkCN (mkN "köök" );
    toaster : CN = mkCN (mkN "röster" );
    water : CN = mkCN (mkN "vesi");
    cleaning : CN = mkCN (mkN "koristamine");
    safeOp : CN = mkCN (mkN "ohutu töö");

    low_power : AP = mkAP (mkA "väike võimsus");
    avgerage_size : AP = mkAP (mkA "keskmine suurus");
    standard : AP = mkAP (mkA "standard");
    fast : AP = mkAP (mkA "kiire");

    make : V2 = mkV2 (mkV "tegema");

    of_Prep : Prep = mkPrep "paljaks";

    -- for dev
    toasterWithMods : CN = mkCN (combineMods low_power average_size) toaster;
    kitchenWithMods : CN = mkCN standard kitchen;
}
