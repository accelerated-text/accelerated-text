const defaultResponses =    require( './response-templates/default' );
const DOCUMENT_PLAN =       require( './data/document-plan' );
const { SELECTORS } =       require( './constants' );


describe( 'default start', () => {

    beforeAll(() => defaultResponses( page ), 10e3 );

    test( 'should not have errors', async () => {

        await expect( page ).not.toMatchElement( SELECTORS.UI_ERROR );
    });

    test( 'should load the document plan', async () => {

        await expect( page ).toMatchElement( `[data-id=${ DOCUMENT_PLAN.documentPlan.srcId }]` );
        await expect( page ).toMatchElement( `[data-id=${ DOCUMENT_PLAN.documentPlan.segments[0].srcId }]` );
    }, 10e3 );

    test( 'should load a variant', async () => {

        await expect( page ).toMatchElement( SELECTORS.VARIANT );

    }, 10e3 );
});
