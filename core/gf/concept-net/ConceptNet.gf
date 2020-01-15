abstract ConceptNet = {
    flags startcat = Message;

    cat Message;
    cat ObjectRef;
    cat LocationData;
    cat LocationDictionary;

    fun AtLocation : ObjectRef -> LocationData -> Message;
    fun VenueRef : ObjectRef;
    fun GeoLoc: LocationData;
    fun PlaceDict: LocationDictionary;
}