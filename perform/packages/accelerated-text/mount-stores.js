const { mount } =           require( '../vesa/' );


module.exports = mount({
    reader:                 require( '../reader/store' ).default,
    user:                   require( '../user/store' ).default,
});
