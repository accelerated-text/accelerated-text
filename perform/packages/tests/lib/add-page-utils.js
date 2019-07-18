const DEFAULT_TIMEOUT = 1e3;

const SELECTOR_WAIT_OPTIONS = {
    timeout:            DEFAULT_TIMEOUT,
};


export default ( t, run, ...args ) =>
    run(
        Object.assign( t, {

            clearInput: async ( selector, page = t.page ) => {
                await page.$eval( selector, el => {
                    el.focus();
                    el.setSelectionRange( 0, el.value.length );
                });
                await page.keyboard.press( 'Backspace' );
            },

            acceptDialog: ( type, message, acceptText, page = t.page ) =>
                Promise.race([
                    new Promise(( resolve, reject ) =>
                        setTimeout(
                            () => reject( Error( `Timeout while waiting for dialog ${ type }(${ message }).` )),
                            DEFAULT_TIMEOUT
                        )
                    ),
                    new Promise(( resolve, reject ) =>
                        page.once( 'dialog', dialog => {
                            const dialogType =      dialog.type();
                            const dialogMessage =   dialog.message();
                            const isExpected = (
                                dialogType === type
                                && (
                                    typeof message !== 'string'
                                    || dialogMessage === message
                                )
                            );
                            if( isExpected ) {
                                resolve( dialog.accept( acceptText ));
                            } else {
                                reject( Error(
                                    `Unexpected dialog. Expected: ${ type }("${ message }"). Got: ${ dialogType }("${ dialogMessage }").`,
                                ));
                            }
                        })
                    ),
                ]),

            findElement: ( selector, page = t.page ) =>
                t.notThrowsAsync( page.waitForSelector( selector, SELECTOR_WAIT_OPTIONS )),

            getElementAttribute: ( selector, attributeName, page = t.page ) =>
                t.findElement( selector, page )
                    .then(() => page.$eval(
                        selector,
                        ( el, attributeName ) => el.getAttribute( attributeName ),
                        attributeName,
                    )),

            getElementProperty: ( selector, propertyName, page = t.page ) =>
                t.findElement( selector, page )
                    .then(() => page.$eval(
                        selector,
                        ( el, propertyName ) => el[propertyName],
                        propertyName,
                    )),

            getElementText: ( selector, page = t.page ) =>
                t.findElement( selector, page )
                    .then(() => page.$eval(
                        selector,
                        el => el.innerText,
                    )),

            getElementValue: ( selector, page = t.page ) =>
                t.findElement( selector, page )
                    .then(() => page.$eval(
                        selector,
                        el => el.value,
                    )),

            notFindElement: ( selector, page = t.page ) =>
                t.throwsAsync( page.waitForSelector( selector, SELECTOR_WAIT_OPTIONS )),

            resetMouse: ( page = t.page ) =>
                page.mouse.move( 0, 0 ),

            retypeElementText: async ( selector, value, options ) => {
                await t.clearInput( selector );
                await t.page.type( selector, value, options );
                await t.page.keyboard.press( 'Control' );
            },

            waitUntilElementGone: async ( selector, timeout = 10e3, page = t.page ) => {

                const timeoutTimestamp =    +new Date + timeout;

                while( +new Date < timeoutTimestamp ) {
                    try {
                        await page.waitForSelector( selector, { timeout: 500 });
                    } catch( _ ) {
                        return;
                    }
                }
                throw Error( `Timeout (${ timeout }ms) exceeded while waiting for selector ${ selector }` );
            },
        }),
        ...args,
    );
