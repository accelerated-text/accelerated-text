abstract Default = {
    flags
        startcat = DocumentPlan01 ;
    cat
        DocumentPlan01 ;
        Segment02 ;
        AtLocAMR ;
        HasPropAMR ;
        LocationDict ;
        LocationData ;
        VenueData ;
    fun
        Function01 : Segment02 -> DocumentPlan01 ;
        Function02 : AtLocAMR -> HasPropAMR -> Segment02 ;
        Function03 : LocationDict -> LocationData -> VenueData -> AtLocAMR ;
        Function04 : HasPropAMR ;

        D01 : LocationDict ;
        D02 : LocationData ;
        D03 : VenueData ;
}

