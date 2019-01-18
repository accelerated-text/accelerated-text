/// Imports --------------------------------------------------------------------

const {
    flatten,
    identity,
} = require( 'ramda' );

const {
    BLOCKLY_ID,
    CHAIN_SEP,
    STATEMENT_SEP,
} = require( './constants' );
const {
    addVertex,
    connectElements,
    isBlock,
    isField,
    isMutation,
    isNext,
    isStatement,
    isValue,
    setProperty,
} = require( './functions' );
const mutations =       require( './mutations' );


/// Mutation converters --------------------------------------------------------

const setMutationProperties = el =>
    [ ...el.attributes ]
        .filter(({ name, value }) => value )
        .map(({ name, value }) => setProperty( name, value ))
        .join( CHAIN_SEP );

const convertMutation = parentEl => mutationEl =>
    [ ...mutationEl.attributes ]
        .filter(({ name, value }) => mutations[name])
        .map(({ name, value }) => mutations[name]( parentEl, value, mutationEl ))
        .filter( identity );

/// Element converters ---------------------------------------------------------

const convertBlock =    ( el, parentEl, parentEdgeType, parentEdgeName ) => {

    const children =    [ ...el.children ];

    return [
        addVertex( el.getAttribute( 'type' )),
        setProperty( BLOCKLY_ID, el.getAttribute( 'id' )),
        ...children
            .filter( isField )
            .map( field =>
                setProperty( field.getAttribute( 'name' ), field.innerText )
            ),
        ...children
            .filter( isMutation )
            .map( setMutationProperties ),
        STATEMENT_SEP,
        ...flatten( children
            .filter( isValue )
            .map( value => [
                convertBlock( value.firstElementChild, el, 'value', value.getAttribute( 'name' )),
                connectElements( 'has-value', el, value.firstElementChild, {
                    name:   value.getAttribute( 'name' ),
                }),
            ])),
        ...flatten( children
            .filter( isStatement )
            .map( statement => [
                convertBlock( statement.firstElementChild, el, 'statement', statement.getAttribute( 'name' )),
                connectElements( 'has-statement', el, statement.firstElementChild, {
                    name:   statement.getAttribute( 'name' ),
                }),
            ])),
        ...flatten( children
            .filter( isNext )
            .map( nextEl => [
                convertBlock( nextEl.firstElementChild, parentEl, parentEdgeType, parentEdgeName ),
                connectElements( 'has-next', el, nextEl.firstElementChild ),
                connectElements( `has-${ parentEdgeType }`, parentEl, nextEl.firstElementChild, {
                    name:   parentEdgeName,
                }),
            ])),
        ...flatten( children
            .filter( isMutation )
            .map( convertMutation( el ))
        ),
    ].join( CHAIN_SEP );
};

/// Exports --------------------------------------------------------------------

module.exports = dom =>
    [ ...dom.children ]
        .filter( isBlock )
        .map( el => convertBlock( el ))
        .join( STATEMENT_SEP );
