import { range }            from 'ramda';

import USER                 from './user';


export const createRecord = ({
    prefix =                'tests-data-data-file-record',
    fieldNames =            [ 'field-one', 'field-two', 'field-three' ],
}) => ({
    __typename:             'Record',
    id:                     `${ prefix }-record`,
    fields:                 fieldNames.map( fieldName => ({
        __typename:         'Field',
        id:                 `${ prefix }-record-field-${ fieldName }`,
        fieldName,
        value:              `${ prefix }-record-field-${ fieldName }-value`,
    })),
});


export const createDataFile = ({
    fieldCount =            3,
    fileName =              'tests-data-data-file-key.csv',
    recordCount =           3,
}) => {
    const id =              `${ USER.id }/${ fileName }`;
    const fieldNames =      range( 0, fieldCount ).map( i => `${ fileName } ${ i }` );
    return {
        __typename:         'DataFile',
        id,
        fieldNames,
        fileName,
        recordCount,
        recordLimit:        recordCount,
        recordOffset:       0,
        records: range( 0, recordCount ).map( i =>
            createRecord({ prefix: id, fieldNames })
        ),
    };
};


export const createDataFileResponse = arg => ({
    getDataFile:            createDataFile( arg ),
});


export default createDataFileResponse({});
