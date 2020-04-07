abstract Simple = {
    flags
        startcat = DocumentPlan01 ;
    cat
        DocumentPlan01 ;
        Segment02 ;
        Frame03 ;
        Frame04 ;
        Operation05 ;
        Operation06 ;
        Operation07 ;
        Operation08 ;
        Operation10 ;
        Operation11 ;
    fun
        Function01 : Segment02 -> DocumentPlan01 ;
        Function02 : Frame03 -> Segment02 ;
        Function03 : Frame04 -> Frame03 ;
        Function04 : Operation05 -> Frame04 ;
        Function05 : Operation06 -> Operation05 ;
        Function06 : Operation07 -> Operation06 ;
        Function07 : Operation10 -> Operation08 -> Operation07 ;
        Function08 : Operation08 ;
        Function09 : Operation11 -> Operation10 ;
        Function10 : Operation11 ;
}