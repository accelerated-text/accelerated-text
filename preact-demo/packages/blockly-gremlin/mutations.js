const {
    identity,
    props,
} = require( 'ramda' );

const {
    CHAIN_SEP,
} = require( './constants' );

const {
    addProperty,
    connectElements,
    connectElementList,
    getValueMap,
    isValue,
} = require( './functions' );


module.exports = {

    else_if_count: ( parentEl, mutationValue ) => {

        const EMPTY_THEN_EXPRESSION =  '';
        const valueMap =    getValueMap( parentEl );
        const findThen =    name => valueMap[`then_${ name.slice( -1 ) }`];

        const isCondition = name => (
            name === 'if'
            || name === 'else'
            || name.startsWith( 'else_if_' )
        );
        const conditions =  Object.keys( valueMap ).filter( isCondition );

        return [
            conditions.map(
                name => connectElements( 'has-condition', parentEl, valueMap[name])
            ),
            conditions.map( name => (
                name === 'else'
                ? addProperty( valueMap[name], 'is-default-condition', true )
                : addProperty( valueMap[name], 'is-condition', true )
            )),
            conditions.map( name => (
                name === 'else'
                ? EMPTY_THEN_EXPRESSION
                : name === 'if'
                ? (
                    valueMap.then
                    ? connectElements( 'then-expression', valueMap.if, valueMap.then )
                    : EMPTY_THEN_EXPRESSION
                )
                : findThen( name ) /// corresponding then_X to else_if_X
                ? connectElements( 'then-expression', valueMap[name], findThen( name ))
                : EMPTY_THEN_EXPRESSION
            )),
            connectElementList( 'has-next', props( conditions, valueMap )),
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
            ),
};
