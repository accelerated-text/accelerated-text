const debugConsole =        require( '../qa-utils/debug-console' );
const nlgMocker =           require( '../nlg-api/response-mocker' );

const { SELECTORS } =       require( './qa.constants' );

describe( 'plan-selector/PlanSelector', () => {

    test( 'should handle empty plan list', async () => {

        debugConsole( page );

        const {
            startMocker,
            stopMocker,
            mockResponse,
        } = nlgMocker( page );

        await startMocker();
        page.goto( TEST_URL );

        await mockResponse( 'GET', '/document-plans/', []);
        await expect( page ).toMatchElement( SELECTORS.BTN_NEW_PLAN );

        await stopMocker();
    }, 10e3 );
});
