const TIMEOUT =             1e3;


const rejectOnTimeout = ( timeout, errorMessage ) =>
    new Promise(( _, reject ) =>
        setTimeout(
            () => reject( Error( errorMessage )),
            timeout,
        ));


const isUnexpectedDialog = ( type, message, dialog ) => {

    const dialogType =      dialog.type();
    const dialogMessage =   dialog.message();
    const isExpected = (
        dialogType === type
        && (
            typeof message !== 'string'
            || dialogMessage === message
        )
    );
    return (
        !isExpected
        && `Unexpected dialog. Expected: ${ type }("${ message }"). Got: ${ dialogType }("${ dialogMessage }").`
    );
};


const onExpectedDialog = ( type, message, page, fn ) =>
    Promise.race([
        rejectOnTimeout(
            TIMEOUT,
            `Timeout while waiting for dialog ${ type }(${ message }).`,
        ),
        new Promise(( resolve, reject ) =>
            page.once( 'dialog', dialog => {
                const errorMessage =    isUnexpectedDialog( type, message, dialog );
                if( errorMessage ) {
                    reject( Error( errorMessage ));
                } else {
                    resolve( fn( dialog ));
                }
            })
        ),
    ]);


export default ( t, run, ...args ) =>
    run(
        Object.assign( t, {

            acceptDialog: ( type, message, acceptText, page = t.page ) =>
                onExpectedDialog( type, message, page,
                    dialog => dialog.accept( acceptText ),
                ),

            dismissDialog: ( type, message, page = t.page ) =>
                onExpectedDialog( type, message, page,
                    dialog => dialog.dismiss(),
                ),
        }),
        ...args,
    );
