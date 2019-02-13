const debugConsole =        require( '../qa-utils/debug-console' );
const nlgMocker =           require( '../nlg-api/response-mocker' );


const TEST_PLAN = {
    id:             'test-id',
    createdAt:      +new Date,
    uid:            'test-uid',
    updateCount:    0,
    name:           'Test plan',
    blocklyXml:     '<xml xmlns="http://www.w3.org/1999/xhtml"><block id="test-document-plan" type="Document-plan" deletable="false"><statement name="segments"><block id="test-segment" type="Segment"><mutation value_count="2" value_sequence="value_"></mutation><field name="text_type">description</field></block></statement></block></xml>',
    documentPlan: {
        type:           'Document-plan',
        srcId:          'test-document-plan',
        segments: [{
            type:       'Segment',
            srcId:      'test-segment',
            text_type:  'description',
            children:   [],
        }],
    },
};


describe( 'augmented-writer/AugmentedWriter', () => {

    test( 'should load document plans and variants', async () => {

        debugConsole( page );

        const {
            startMocker,
            stopMocker,
            mockResponse,
        } = nlgMocker( page );

        await startMocker();
        page.goto( TEST_URL );

        await mockResponse( 'GET', '/document-plans/', [ TEST_PLAN ]);

        await mockResponse( 'GET', `/document-plans/${ TEST_PLAN.id }/variants`, []);

        await expect( page ).toMatchElement( `[data-id=${ TEST_PLAN.documentPlan.srcId }]` );
        await expect( page ).toMatchElement( `[data-id=${ TEST_PLAN.documentPlan.segments[0].srcId }]` );

        await stopMocker();
    }, 10e3 );
});
