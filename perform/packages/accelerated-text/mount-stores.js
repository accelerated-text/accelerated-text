const { mount } =           require( '../vesa/' );


module.exports = mount({
    contexts:               require( '../contexts/store' ).default,
    dataSamples:            require( '../data-samples/store' ).default,
    documentPlans:          require( '../document-plans/store' ).default,
    lexicon:                require( '../lexicon/store' ).default,
    planList:               require( '../plan-list/store' ).default,
    reader:                 require( '../reader/store' ).default,
    user:                   require( '../user/store' ).default,
    variantsApi:            require( '../variants-api/store' ).default,
}, [
    require( '../contexts/adapter' ).default,
    require( '../data-samples/adapter' ).default,
    require( '../document-plans/adapter' ).default,
    require( '../plan-list/adapter' ).default,
    require( '../variants-api/adapter' ).default,

    require( '../plan-list/local-storage-adapter' ).default,
]);
