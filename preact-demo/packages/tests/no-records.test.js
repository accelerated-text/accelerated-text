const noRecords =           require( './response-templates/no-records' );


const { SELECTORS } =       require( './constants' );


describe( 'no records', () => {

    beforeEach(() => noRecords( page ), 10e3 );

    test( 'should handle empty plan list', async () => {

        await expect( page ).toMatchElement( SELECTORS.BTN_NEW_PLAN );

    }, 10e3 );
});
