export default {
    id:             undefined,
    createdAt:      undefined,
    uid:            null,
    updateCount:    0,
    contextId:      null,
    dataSampleId:   null,
    dataSampleRow:  0,
    name:           'Untitled plan',
    blocklyXml:     '<xml xmlns="http://www.w3.org/1999/xhtml"><block type="Document-plan" deletable="false"><statement name="segments"><block type="Segment"><mutation value_count="2" value_sequence="value_"></mutation><field name="text_type">description</field></block></statement></block></xml>',
    documentPlan: {
        type:           'Document-plan',
        srcId:          'new-document-plan',
        segments: [{
            type:       'Segment',
            srcId:      'new-segment',
            text_type:  'description',
            children:   [],
        }],
    },
};
