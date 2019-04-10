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

    t.page.on( 'console', onConsole );

    if( run ) {
        await run( t, ...args );
    }

    t.page.off( 'console', onConsole );
};
