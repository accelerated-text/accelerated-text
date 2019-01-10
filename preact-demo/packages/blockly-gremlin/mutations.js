const { identity } =    require( 'ramda' );

const {
    CHAIN_SEP,
} = require( './constants' );

const {
    connectElements,
    isValue,
} = require( './functions' );


module.exports = {

    next_values: ( parentEl, mutationValue ) =>
        [ ...parentEl.children ]
            .filter( isValue )
            .filter( valueEl =>
                valueEl.getAttribute( 'name' )
                    .startsWith( mutationValue )
            )
            .map(( valueEl, idx, array ) =>
                idx > 0 && connectElements(
                    'has-next',
                    array[idx - 1].firstElementChild,
                    valueEl.firstElementChild
                ).join( CHAIN_SEP )
            ).filter( identity )
            .join( CHAIN_SEP ),
};
