const DATA_FILE =   require( './data-file' );


const PLAN_ID =     'tests-document-plan-id';
const SEGMENT_ID =  'tests-segment';


module.exports = {
    id:             'tests-data-document-plan-id',
    createdAt:      +new Date,
    uid:            'tests-document-plan-uid',
    updateCount:    0,
    contextId:      'tests-context-id',
    dataSampleId:   DATA_FILE.key,
    name:           'Test plan',
    blocklyXml:     `<xml xmlns="http://www.w3.org/1999/xhtml"><block id="${ PLAN_ID }" type="Document-plan" deletable="false"><statement name="segments"><block id="${ SEGMENT_ID }" type="Segment"><mutation value_count="2" value_sequence="value_"></mutation><field name="text_type">description</field></block></statement></block></xml>`,
    documentPlan: {
        type:           'Document-plan',
        srcId:          PLAN_ID,
        segments: [{
            type:       'Segment',
            srcId:      SEGMENT_ID,
            text_type:  'description',
            children:   [],
        }],
    },
};
