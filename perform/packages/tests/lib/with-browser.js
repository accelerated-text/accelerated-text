import launchBrowser        from './launch-browser';


export default async ( t, run, ...args ) => {

    const browser =         await launchBrowser();

    try {
        await run( Object.assign( t, { browser }), ...args );
    } catch( err ) {
        /// ignore
    } finally {
        await browser.close();
    }
};
