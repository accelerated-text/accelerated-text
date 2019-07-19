import disableApolloAd      from './disable-apollo-devtools-ad';


export default async ( t, run, ...args ) => {

    const page =            await t.browser.newPage();

    disableApolloAd( page );

    try {
        await run( Object.assign( t, { page }), ...args );
    } catch( err ) {
        t.log( 'Error:', err );
    } finally {
        await page.close();
    }
};
