abstract Default = {
    flags
        startcat = DocumentPlan01 ;
    cat
        DocumentPlan01 ;
        Segment02 ;
        Amr03 ;
        Amr07 ;
    fun
        Function01 : Segment02 -> DocumentPlan01 ;
        Function02 : Amr03 -> Amr07 -> Segment02 ;
        Function03 : Amr03 ;
        Function04 : Amr07 ;
}
