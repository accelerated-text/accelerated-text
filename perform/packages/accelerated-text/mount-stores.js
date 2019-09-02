const { mount } =           require( '../vesa/' );


module.exports = mount({
    dataSamples:            require( '../data-samples/store' ).default,
    documentPlans:          require( '../document-plans/store' ).default,
    planList:               require( '../plan-list/store' ).default,
    reader:                 require( '../reader/store' ).default,
    user:                   require( '../user/store' ).default,
    variantsApi:            require( '../variants/store' ).default,
}, [
    require( '../data-samples/adapter' ).default,
    require( '../document-plans/adapter' ).default,
    require( '../plan-list/adapter' ).default,
    require( '../variants/adapter' ).default,

    require( '../plan-list/local-storage-adapter' ).default,
]);
