const debugConsole =        require( '../qa-utils/debug-console' );
const nlgMocker =           require( '../nlg-api/response-mocker' );
const requestInterceptor =  require( '../qa-utils/request-interceptor' );

const { SELECTORS } =       require( './qa.constants' );

describe( 'plan-selector/PlanSelector', () => {

    test( 'should handle empty plan list', async () => {

        debugConsole( page );

        /*
        const {
            startMocker,
            stopMocker,
            mockResponse,
        } = nlgMocker( page );

        await startMocker();
        */
        const {
            continueAll,
            provideOnce,
            stopInterception,
        } = await requestInterceptor( page );

        const nlgProvideOnce =  nlgMocker( provideOnce );

        continueAll( 'GET', new RegExp( `${ TEST_URL }/.*` ));

        page.goto( TEST_URL );

        await Promise.all([
            nlgProvideOnce( 'GET', '/data/', []),
            nlgProvideOnce( 'GET', '/document-plans/', []),
        ]);

        /*
        await mockResponse( 'GET', '/data/', []);
        await mockResponse( 'GET', '/document-plans/', []);
        */
        await expect( page ).toMatchElement( SELECTORS.BTN_NEW_PLAN );

        ///await stopMocker();
        await stopInterception( page );
    }, 10e3 );
});
