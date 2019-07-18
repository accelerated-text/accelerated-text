const SELECTOR_WAIT_OPTIONS = {
    timeout:            1e3,
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

            findElement: ( selector, page = t.page ) =>
                t.notThrowsAsync( page.waitForSelector( selector, SELECTOR_WAIT_OPTIONS )),

            getElementAttribute: ( selector, attributeName, page = t.page ) =>
                page.evaluate(
                    ( selector, attributeName ) =>
                        document.querySelector( selector ).getAttribute( attributeName ),
                    selector,
                    attributeName,
                ),

            getElementProperty: ( selector, propertyName, page = t.page ) =>
                page.evaluate(
                    ( selector, propertyName ) =>
                        document.querySelector( selector )[propertyName],
                    selector,
                    propertyName,
                ),

            getElementText: ( selector, page = t.page ) =>
                page.evaluate(
                    selector => document.querySelector( selector ).innerText,
                    selector,
                ),

            getElementValue: ( selector, page = t.page ) =>
                page.evaluate(
                    selector => document.querySelector( selector ).value,
                    selector,
                ),

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
