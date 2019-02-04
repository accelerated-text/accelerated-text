const { QA, SELECTORS } =   require( '../qa.constants' );


describe( 'plan-editor/onboard/workflow', () => {

    beforeEach( async () => {

        await page.goto( TEST_URL, { waitUntil: 'load' });
    }, 10e3 );

    test( 'Default workflow', async () => {

        const body =    await expect( page ).toMatchElement( SELECTORS.BODY );
        const header =  await expect( page ).toMatchElement( SELECTORS.HEADER );

        /// Click [Upload] button and it disappears:
        await expect( body ).toClick( SELECTORS.UPLOAD_SAMPLE );
        await expect( body ).not.toMatchElement( SELECTORS.UPLOAD_SAMPLE );

        /// Select context and it disappears:
        await expect( body ).toSelect( SELECTORS.SELECT_CONTEXT, 'T-Shirts' );
        await expect( body ).not.toMatchElement( SELECTORS.SELECT_CONTEXT );

        /// Click [Add] button and it disappears:
        await expect( body ).toClick( SELECTORS.ADD_EXAMPLE );
        await expect( body ).not.toMatchElement( SELECTORS.ADD_EXAMPLE );

        await expect( header ).toMatchElement( SELECTORS.UPLOAD_SAMPLE );
        await expect( header ).toMatchElement( SELECTORS.SELECT_CONTEXT );

        await expect( body ).toMatchElement( `[data-id=${ QA.EXAMPLE_XML }]` );
    }, 10e3 );
});
