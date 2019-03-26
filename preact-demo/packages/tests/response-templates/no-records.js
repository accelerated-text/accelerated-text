const debugConsole =        require( '../../qa-utils/debug-console' );
const nlgProvide =          require( '../../nlg-api/provide-response' );
const requestInterceptor =  require( '../../qa-utils/request-interceptor' );

const USER =                require( '../data/user' );


module.exports = async page => {

    debugConsole( page );

    const interceptor =     await requestInterceptor( page );
    const {
        continueAll,
        provideOnce,
        stopInterception,
    } = interceptor;
    const nlgProvideOnce =  nlgProvide( provideOnce );

    continueAll( 'GET', new RegExp( `${ TEST_URL }/.*` ));

    page.goto( TEST_URL );

    await Promise.all([
        nlgProvideOnce( 'GET', `/data/?user=${ USER.id }`, []),
        nlgProvideOnce( 'GET', '/document-plans/', []),
    ]);

    await stopInterception( page );

    return {
        ...interceptor,
        nlgProvideOnce,
    };
};
