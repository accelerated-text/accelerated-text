/*  eslint-disable no-console */

module.exports = page => {

    page.on( 'console', async message => {
        const text = message.text();
        const handle = message.args()[0];
        console.log( 'ON CONSOLE', message.type(), text );
        if( handle ) {
            const value =       await handle.jsonValue();
            console.log( value );
            const properties =  await handle.getProperties();
            properties.forEach(( val, key ) => {
                console.log( key, typeof val, val.toString());
            });
        }
    });
};
