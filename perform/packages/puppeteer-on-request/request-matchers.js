module.exports = () => {

    const matchers =    [];

    return {
        reset: () =>
            matchers.splice( 0, matchers.length - 1 ),

        add: ( match, fields ) =>
            matchers.unshift({ match, ...fields }),

        findMatch: request =>
            matchers.find(({ match }) => match( request )),

        remove: matcher => {
            const idx = matchers.indexOf( matcher );
            if( idx > 0 ) {
                matchers.splice( idx, 1 );
            }
        },
    };
};
