/// Imports --------------------------------------------------------------------

const {
    flatten,
    identity,
    toPairs,
} = require( 'ramda' );

/// Constants ------------------------------------------------------------------

const BLOCKLY_ID =      'blockly-id';
const CHAIN_SEP =       '';
const GLOBAL =          'g.traversal()';
const STATEMENT_SEP =   ';\n';

/// Element type tests ---------------------------------------------------------

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

/// Mutation converters --------------------------------------------------------

const setMutationProperties = el =>
    [ ...el.attributes ]
        .filter(({ name, value }) => value )
        .map(({ name, value }) => setProperty( name, value ))
        .join( CHAIN_SEP );

const convertMutations = parentEl => mutationEl =>
    [ ...mutationEl.attributes ]
        .map(({ name, value }) => {
            switch( name ) {
            case 'next_values':
                return [ ...parentEl.children ]
                    .filter( isValue )
                    .filter( valueEl => valueEl.getAttribute( 'name' ).startsWith( value ))
                    .map(( valueEl, idx, array ) =>
                        idx && connectElements( 'has-next', array[idx - 1].firstElementChild, valueEl.firstElementChild )
                    ).filter( identity )
                    .join( STATEMENT_SEP );
            default:
                return '';
            }
        })
        .filter( identity )
        .join( STATEMENT_SEP );

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
        ...children
            .filter( isMutation )
            .map( convertMutations( el )),
    ].join( CHAIN_SEP );
};

/// Exports --------------------------------------------------------------------

module.exports = dom =>
    [ ...dom.children ]
        .filter( isBlock )
        .map( el => convertBlock( el ))
        .join( STATEMENT_SEP );
