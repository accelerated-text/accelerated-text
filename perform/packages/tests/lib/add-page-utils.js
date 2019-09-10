const DEFAULT_TIMEOUT =     8e3;

const SELECTOR_WAIT_OPTIONS = {
    timeout:                2e3,
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

            findElements: ( selectors, presence, page = t.page ) =>
                Promise.all( Object.entries( presence )
                    .map(([ sel, shouldFind ]) =>
                         shouldFind
                            ? t.findElement( selectors[sel], page )
                            : t.notFindElement( selectors[sel], page )
                    )
                ),

            getElementAttribute: ( selector, attributeName, page = t.page ) =>
                t.findElement( selector, page )
                    .then(() => page.$eval(
                        selector,
                        ( el, attributeName ) => el.getAttribute( attributeName ),
                        attributeName,
                    )),

            getElementCenter: ( selector, page = t.page ) =>
                t.findElement( selector, page )
                    .then(() => page.$( selector ))
                    .then( elHandle => elHandle.boundingBox())
                    .then( box => ({
                        x:      box.x + box.width / 2,
                        y:      box.y + box.height / 2,
                    })),

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

            notFindElement: async ( selector, page = t.page ) => {
                try {
                    const el =          await page.waitForSelector( selector, SELECTOR_WAIT_OPTIONS );
                    const html =        await el.getProperty( 'outerHTML' );
                    const htmlString =  await html.jsonValue();
                    t.fail( `Found unexpected element for ${ selector }:\n${ htmlString }` );
                    await html.dispose();
                } catch( err ) {
                    t.pass( `Element not found ${ selector }.` );
                }
            },

            resetMouse: ( page = t.page ) =>
                page.mouse.move( 0, 0 ),

            retypeElementText: async ( selector, value, options ) => {
                await t.clearInput( selector );
                await t.page.type( selector, value, options );
                await t.page.keyboard.press( 'Control' );
            },

            waitUntilElementGone: async ( selector, timeout = DEFAULT_TIMEOUT, page = t.page ) => {

                const timeoutTimestamp =    +new Date + timeout;

                t.timeout( timeout + 2e3 );
                while( +new Date < timeoutTimestamp ) {
                    try {
                        await page.waitForSelector( selector, { timeout: 500 });
                    } catch( _ ) {
                        t.timeout( DEFAULT_TIMEOUT );
                        return;
                    }
                }
                throw Error( `Timeout (${ timeout }ms) exceeded while waiting for selector ${ selector }` );
            },
        }),
        ...args,
    );
