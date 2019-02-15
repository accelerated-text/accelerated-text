const devTools = (
    typeof window !== 'undefined'
    && window
    && window.__REDUX_DEVTOOLS_EXTENSION__  //  eslint-disable-line no-underscore-dangle
);


export default ( storeName, storeId ) => {

    if( !storeName || !devTools ) {
        return {
            onEvent:        () => null,
            tapInit:        state => state,
            unsubscribe:    () => null,
        };
    }

    const name =            `${ storeName } [${ storeId }]`;
    const toolsInstance =   devTools.connect({ name });

    const onEvent = ( eventNamespace, eventName, eventId, state ) => {
        toolsInstance.send(
            `${ eventNamespace }.${ eventName }[${ eventId }]`,
            state,
        );
    };

    const tapInit = state => {
        toolsInstance.init( state );
        return state;
    };

    const unsubscribe =     toolsInstance.unsubscribe.bind( toolsInstance );

    return {
        onEvent,
        tapInit,
        unsubscribe,
    };
};
