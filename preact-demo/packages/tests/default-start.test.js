const defaultResponses =    require( './response-templates/default' );
const DOCUMENT_PLAN =       require( './data/document-plan' );


describe( 'default start', () => {

    beforeEach(() => defaultResponses( page ), 10e3 );

    test( 'should load document plans and variants', async () => {

        await expect( page ).toMatchElement( `[data-id=${ DOCUMENT_PLAN.documentPlan.srcId }]` );
        await expect( page ).toMatchElement( `[data-id=${ DOCUMENT_PLAN.documentPlan.segments[0].srcId }]` );
    }, 10e3 );
});
