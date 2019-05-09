export default async ( t, run, ...args ) => {

    const onConsole = async message => {

        const handle =      message.args()[0];

        if( !handle ) {
            const text =    message.text();
            t.log( 'on console', message.type(), text );
        } else {
            const value =   await handle.jsonValue();
            t.log( 'on console', message.type(), value );
        }
    };

    const onError = async error => {

        t.log( 'Error:', error.message );
    };

    t.page.on( 'console', onConsole );
    t.page.on( 'pageerror', onError );

    if( run ) {
        await run( t, ...args );
    }

    t.page.off( 'console', onConsole );
};
