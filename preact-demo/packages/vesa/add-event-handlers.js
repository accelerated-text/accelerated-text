import * as R           from 'ramda';


const createFn = ( dispatcher, eventName, eventNamespace ) => {

    const fn =  arg => dispatcher.dispatch({ arg, eventName, eventNamespace });

    return Object.assign( fn, {
        async:  arg => setTimeout( fn, 0, arg ),
    });
};


export default ( E, eventHandlers, dispatcher ) =>
    R.mergeDeepLeft(
        E || {},
        R.mapObjIndexed(
            ( fns, eventNamespace ) => R.mapObjIndexed(
                ( fn, eventName ) => createFn( dispatcher, eventName, eventNamespace ),
                fns,
            ),
            eventHandlers,
        ),
    );
