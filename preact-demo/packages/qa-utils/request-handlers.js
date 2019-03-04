const isMatch = ( method, url ) => matcher => (
    ( matcher.method === method
        || ( matcher.method.test && matcher.method.test( method )))
    && ( matcher.url === url
        || ( matcher.url.test && matcher.url.test( url )))
);


module.exports = () => {

    const handlers =    [];

    return {
        reset: () =>
            handlers.splice( 0, handlers.length - 1 ),

        add: ( method, url, handler ) =>
            handlers.unshift({ method, url, ...handler }),

        findMatch: ( method, url ) =>
            handlers.find( isMatch( method, url )),

        remove: handler => {
            const idx = handlers.indexOf( handler );
            if( idx >= 0 ) {
                handlers.splice( idx, 1 );
            }
        },
    };
};
