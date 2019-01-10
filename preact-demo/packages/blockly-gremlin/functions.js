const {
    toPairs,
} = require( 'ramda' );

const {
    BLOCKLY_ID,
    CHAIN_SEP,
    GLOBAL,
    STATEMENT_SEP,
} = require( './constants' );


const isBlock =         el => el.tagName.toLowerCase() === 'block';
const isField =         el => el.tagName.toLowerCase() === 'field';
const isMutation =      el => el.tagName.toLowerCase() === 'mutation';
const isNext =          el => el.tagName.toLowerCase() === 'next';
const isStatement =     el => el.tagName.toLowerCase() === 'statement';
const isValue =         el => el.tagName.toLowerCase() === 'value';

/// Base Gremlin generators ----------------------------------------------------

const addEdge =         ( type, from, to ) =>
    `${ GLOBAL }.addE('${ type }').from(${ from }).to(${ to })`;

const addVertex =       type =>
    `${ GLOBAL }.addV('${ type }')`;

const setProperty =     ( name, value ) =>
    `.property('${ name }', '${ value }')`;

const vById =           id =>
    `${ GLOBAL }.V().has('${ BLOCKLY_ID }', '${ id }').next()`;

/// Complex Gremlin generators -------------------------------------------------

const objToProperties = ( obj = {}) =>
    toPairs( obj )
        .map(([ k, v ]) => setProperty( k, v ))
        .join( CHAIN_SEP );

const connectElements = ( type, fromEl, toEl, properties ) => [
    addEdge(
        type,
        vById( fromEl.getAttribute( 'id' )),
        vById( toEl.getAttribute( 'id' )),
    ),
    objToProperties( properties ),
    STATEMENT_SEP,
];


module.exports = {
    isBlock,
    isField,
    isMutation,
    isNext,
    isStatement,
    isValue,
    addEdge,
    addVertex,
    setProperty,
    vById,
    objToProperties,
    connectElements,
};
