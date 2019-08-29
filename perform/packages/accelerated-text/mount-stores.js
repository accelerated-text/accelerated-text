const { mount } =           require( '../vesa/' );


module.exports = mount({
    documentPlans:          require( '../document-plans/store' ).default,
    planList:               require( '../plan-list/store' ).default,
    reader:                 require( '../reader/store' ).default,
    user:                   require( '../user/store' ).default,
    variantsApi:            require( '../variants/store' ).default,
}, [
    require( '../document-plans/adapter' ).default,
    require( '../plan-list/adapter' ).default,
    require( '../variants/adapter' ).default,

    require( '../plan-list/local-storage-adapter' ).default,
]);
