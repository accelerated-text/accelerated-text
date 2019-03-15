module.exports = {
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
