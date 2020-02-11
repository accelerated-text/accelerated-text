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
    safe_operation : CN = mkCN (mkN "ohutu töö");
    auto_switch : CN =  mkCN (mkN "automaatne väljalülitamin");
    kettle : CN =  mkCN (mkN "veekeetja");
    fridge : CN =  mkCN (mkN "külmik");
    steel : CN =  mkCN (mkN "terasest");
    removable_filter : CN = mkCN (mkN "eemaldatav filter");
    small : AP = mkAP (mkA "väike");
    regular : AP = mkAP (mkA "regulaarne");

    easy_N : N = mkN "lihtne";

    low_power : AP = mkAP (mkA "väike võimsus");
    average_size : AP = mkAP (mkA "keskmine suurus");
    standard : AP = mkAP (mkA "standard");
    fast : AP = mkAP (mkA "kiire");

    make : V2 = mkV2 (mkV "tegema");

    -- for dev
    toasterWithMods : CN = mkCN (combineMods low_power average_size) toaster;
    kitchenWithMods : CN = mkCN standard kitchen;
}
