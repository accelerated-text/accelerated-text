abstract Default = {
    flags
        startcat = DocumentPlan01 ;
    cat
        DocumentPlan01 ;
        Segment02 ;
        
        AtLocationSnt ;
        HasPropertySnt ;
        PropertyMod ;
        
        LocationDict ;
        LocationData ;
        VenueData ;
        Polarity ; Property ;
    fun
        Function01 : Segment02 -> DocumentPlan01 ;

        Function02 : AtLocationSnt -> HasPropertySnt -> Segment02 ;
        Function021 : AtLocationSnt -> Segment02 ;

        Function03 : LocationDict -> LocationData -> VenueData -> AtLocationSnt ;
        Function031 : LocationDict -> LocationData -> VenueData -> PropertyMod -> AtLocationSnt ;
        Function04 : HasPropertySnt ;

        FunctionAMRHasProp : Property -> Polarity -> PropertyMod;

        D01 : LocationDict ;
        D02 : LocationData ;
        D03 : VenueData ;
        D04 : Polarity;
        D05 : Property;
}

