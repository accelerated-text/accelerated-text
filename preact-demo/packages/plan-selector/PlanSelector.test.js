const debugConsole =        require( '../qa-utils/debug-console' );
const nlgProvide =          require( '../nlg-api/provide-response' );
const requestInterceptor =  require( '../qa-utils/request-interceptor' );

const { SELECTORS } =       require( './qa.constants' );

describe( 'plan-selector/PlanSelector', () => {

    test( 'should handle empty plan list', async () => {

        debugConsole( page );

        const {
            continueAll,
            provideOnce,
            stopInterception,
        } = await requestInterceptor( page );
        const nlgProvideOnce =  nlgProvide( provideOnce );

        continueAll( 'GET', new RegExp( `${ TEST_URL }/.*` ));

        page.goto( TEST_URL );

        await Promise.all([
            nlgProvideOnce( 'GET', '/data/', []),
            nlgProvideOnce( 'GET', '/document-plans/', []),
        ]);

        await expect( page ).toMatchElement( SELECTORS.BTN_NEW_PLAN );

        await stopInterception( page );
    }, 10e3 );
});
