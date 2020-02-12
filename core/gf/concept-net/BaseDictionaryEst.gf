resource BaseDictionaryEst = open ResEst, ParadigmsEst, SyntaxEst, UtilsEst in {
  oper

    suitable : A2 = mkA2 (mkA ("sobivad")) for_Prep;
    allows: V2 = mkV2 (mkV "lube") for_Prep;
    features: V2 = mkV2 (mkV "näojooned");
    includes: V2 = mkV2 (mkV "sisaldama");

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
    door: CN =  mkCN (mkN "uks");
    steel : CN =  mkCN (mkN "terasest");
    wood : CN =  mkCN (mkN "puit");
    removable_filter : CN = mkCN (mkN "eemaldatav filter");
    package : CN = mkCN (mkN "pakett");
    interior_use : CN = mkCN (mkN "siseruumides kasutamiseks");

    easy_N : N = mkN "lihtne";

    low_power : A = (mkA "väike võimsus");
    average_size : A = (mkA "keskmine suurus");
    standard : A = (mkA "standard");
    fast : A = (mkA "kiire");
    small : A = (mkA "väike");
    regular : A = (mkA "regulaarne");

    make : V2 = mkV2 (mkV "tegema");

    -- for dev
    -- toasterWithMods : CN = mkCN (combineMods low_power average_size) toaster;
    -- kitchenWithMods : CN = mkCN standard kitchen;
}
