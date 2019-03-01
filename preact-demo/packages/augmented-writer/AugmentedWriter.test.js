const debugConsole =        require( '../qa-utils/debug-console' );
const nlgMocker =           require( '../nlg-api/response-mocker' );
const requestInterceptor =  require( '../qa-utils/interceptor' );


const DATA_FILE = {
    key:            'test-data-item-key',
    fieldNames:     [ 'First', 'Second', 'Third' ],
};

const TEST_PLAN = {
    id:             'test-id',
    createdAt:      +new Date,
    uid:            'test-uid',
    updateCount:    0,
    contextId:      'test-context-id',
    dataSampleId:   'test-data-sample-id',
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
const TEST_RESULT = {
    resultId:       'test-result-id',
};


describe( 'augmented-writer/AugmentedWriter', () => {

    test( 'should load document plans and variants', async () => {

        debugConsole( page );

        const {
            continueAll,
            provideOnce,
            stopInterception,
        } = await requestInterceptor( page );

        const nlgProvideOnce =  nlgMocker( provideOnce );

        continueAll( 'GET', new RegExp( `${ TEST_URL }/.*` ));

        page.goto( TEST_URL );

        await Promise.all([
            nlgProvideOnce( 'GET', '/data/', [ DATA_FILE ]),
            nlgProvideOnce( 'GET', '/document-plans/', [ TEST_PLAN ])
                .then(() => nlgProvideOnce( 'POST', '/nlg/', TEST_RESULT ))
                .then(() => nlgProvideOnce( 'GET', `/nlg/${ TEST_RESULT.resultId }`, {
                    key:        TEST_RESULT.resultId,
                    ready:      true,
                    variants:   [],
                    updatedAt:  +new Date,
                })),
        ]);

        await expect( page ).toMatchElement( `[data-id=${ TEST_PLAN.documentPlan.srcId }]` );
        await expect( page ).toMatchElement( `[data-id=${ TEST_PLAN.documentPlan.segments[0].srcId }]` );

        await stopInterception( page );
    }, 10e3 );
});
