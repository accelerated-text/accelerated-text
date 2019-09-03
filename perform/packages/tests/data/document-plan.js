import DATA_FILE_LIST   from './data-file-list';


const DATA_FILES =      DATA_FILE_LIST.listDataFiles.dataFiles;


export const createDocumentPlan = ({
    id =                'tests-data-document-plan-id',
    uid =               'tests-data-document-plan-uid',
    srcId =             'tests-data-document-plan-src-id',
    segmentId =         'tests-data-document-plan-segment-id',
}) => ({
    id,
    createdAt:      +new Date,
    uid,
    updateCount:    0,
    contextId:      'tests-context-id',
    dataSampleId:   DATA_FILES[0].id,
    dataSampleRow:  0,
    name:           'Test plan',
    blocklyXml:     `<xml xmlns="http://www.w3.org/1999/xhtml"><block id="${ srcId }" type="Document-plan" deletable="false"><statement name="segments"><block id="${ segmentId }" type="Segment"><mutation value_count="2" value_sequence="value_"></mutation><field name="text_type">description</field></block></statement></block></xml>`,
    documentPlan: {
        type:           'Document-plan',
        srcId,
        segments: [{
            type:       'Segment',
            srcId:      segmentId,
            text_type:  'description',
            children:   [],
        }],
    },
});


export default createDocumentPlan({});
