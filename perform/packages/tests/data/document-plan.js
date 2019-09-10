import DATA_FILE_LIST       from './data-file-list';


const DATA_FILES =          DATA_FILE_LIST.listDataFiles.dataFiles;


export const createDocumentPlan = ( prefix = 'tests-data-document-plan' ) => {

    const id =              `${ prefix }-id`;
    const uid =             `${ prefix }-uid`;
    const name =            `${ prefix } name`;
    const srcId =           `${ prefix }-src-id`;
    const segmentId =       `${ prefix }-segment-id`;

    return ({
        __typename:         'DocumentPlan',
        id,
        uid,

        createdAt:          +new Date - 10e3,
        dataSampleId:       DATA_FILES[0].id,
        dataSampleRow:      0,
        name,
        updateCount:        1,
        updatedAt:          +new Date,

        blocklyXml:         `<xml xmlns="http://www.w3.org/1999/xhtml"><block id="${ srcId }" type="Document-plan" deletable="false"><statement name="segments"><block id="${ segmentId }" type="Segment"><mutation value_count="2" value_sequence="value_"></mutation><field name="text_type">description</field></block></statement></block></xml>`,
        documentPlan: JSON.stringify({
            type:           'Document-plan',
            srcId,
            segments: [{
                type:       'Segment',
                srcId:      segmentId,
                text_type:  'description',
                children:   [],
            }],
        }),
    });
};


export default createDocumentPlan();
