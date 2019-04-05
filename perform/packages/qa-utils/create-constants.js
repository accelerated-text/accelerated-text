const { mapObjIndexed } =   require( 'ramda' );


module.exports = ( prefix, names ) => ({
    PREFIX:     prefix,
    QA: mapObjIndexed(
        name => `qa-${ prefix }-${ name }`,
        names,
    ),
    SELECTORS: mapObjIndexed(
        name => `.qa-${ prefix }-${ name }`,
        names,
    ),
});
