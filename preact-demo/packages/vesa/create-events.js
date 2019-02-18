import debug        from 'debug';


let counter =   0;


const createFn = ( dispatcher, eventName, eventNamespace ) => {

    const eventId = counter += 1;
    const log =     debug( `E.${ eventNamespace }.${ eventName }[${ eventId }]` );

    const fn =      arg => {
        log( arg );
        dispatcher.dispatch({ arg, eventId, eventName, eventNamespace });
    };

    return Object.assign( fn, {
        eventId,
        async:      arg => setTimeout( fn, 0, arg ),
    });
};


export default ( oldE, eventHandlers, dispatcher ) => {

    const newE =    { ...oldE };

    for( const eventNamespace in eventHandlers ) {
        newE[eventNamespace] =  newE[eventNamespace] || {};
        for( const eventName in eventHandlers[eventNamespace]) {
            newE[eventNamespace][eventName] = (
                newE[eventNamespace][eventName]
                || createFn( dispatcher, eventName, eventNamespace )
            );
        }
    }

    return newE;
};
