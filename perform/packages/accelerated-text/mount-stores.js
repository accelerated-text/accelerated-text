const { mount } =           require( '../vesa/' );


module.exports = mount({
    reader:                 require( '../reader/store' ).default,
});
