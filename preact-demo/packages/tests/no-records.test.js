const noRecords =           require( './response-templates/no-records' );

const { SELECTORS } =       require( './constants' );


describe( 'no records', () => {

    beforeAll(() => noRecords( page ), 10e3 );

    test( 'should not have errors', async () => {

        await expect( page ).not.toMatchElement( SELECTORS.UI_ERROR );
    });

    test( 'should handle empty plan list', async () => {

        await expect( page ).toMatchElement( SELECTORS.BTN_NEW_PLAN );

    }, 10e3 );
});
