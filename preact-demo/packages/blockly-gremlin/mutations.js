const {
    identity,
    times,
} = require( 'ramda' );

const {
    CHAIN_SEP,
    STATEMENT_SEP,
} = require( './constants' );

const {
    connectElements,
    getValueMap,
    isValue,
    setProperty,
    vById,
} = require( './functions' );


module.exports = {

    else_if_count: ( parentEl, mutationValue ) => {

        const valueMap =    getValueMap( parentEl );
        console.log( valueMap );
        const elseIfEl =    n => valueMap[`else_if_${ n }`];
        const thenEl =      n => valueMap[`then_${ n }`];
        const addProperty = ( el, name, value ) =>
            vById( el.id ) + setProperty( name, value ) + STATEMENT_SEP;

        return [
            addProperty( valueMap.if, 'is-condition', true ),
            connectElements( 'has-condition', parentEl, valueMap.if ),
            connectElements( 'then-expression', valueMap.if, valueMap.then ),
            ...times(
                n => [
                    addProperty( elseIfEl( n ), 'is-condition', true ),
                    connectElements( 'has-condition', parentEl, elseIfEl( n )),
                    connectElements( 'then-expression', elseIfEl( n ), thenEl( n )),
                    n
                        ? connectElements( 'has-next', elseIfEl( n - 1 ), elseIfEl( n ))
                        : connectElements( 'has-next', valueMap.if, elseIfEl( n )),
                ],
                mutationValue,
            ),
            addProperty( valueMap.else, 'is-default-condition', true ),
            mutationValue
                ? connectElements( 'has-next', elseIfEl( mutationValue - 1 ), valueMap.else )
                : connectElements( 'has-next', valueMap.if, valueMap.else ),
        ];
    },

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
