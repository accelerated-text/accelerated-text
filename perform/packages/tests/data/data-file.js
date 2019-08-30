import { basename }         from 'path';
import { range }            from 'ramda';

import USER                 from './user';


export const createField = ({
    prefix =                'tests-data-data-file-field',
    fieldName =             'tests-data-data-file-field-name',
}) => ({
    __typename:             'Field',
    id:                     `${ prefix }-field-${ fieldName }`,
    fieldName,
    value:                  `${ prefix }-field-${ fieldName }-value`,
});


export const createRecord = ({
    prefix =                'tests-data-data-file-record',
    fieldNames =            [ 'field-one', 'field-two', 'field-three' ],
}) => ({
    __typename:             'Record',
    id:                     `${ prefix }-record`,
    fields: fieldNames.map( fieldName =>
        createField({
            prefix:         `${ prefix }-record`,
            fieldName,
        })),
});


export const createDataFilePartial = ({
    fieldCount =            3,
    fileName =              'tests-data-data-file-key.csv',
    id =                    null,
}) => ({
    __typename:             'DataFile',
    id:                     id || `${ USER.id }/${ fileName }`,
    fileName:               fileName || basename( id ),
    fieldNames: range( 0, fieldCount )
        .map( i => `${ fileName }-${ i }` ),
});


export const createDataFileFull = (
    partial,
    { prefix, recordCount = 3 } = {},
) => ({
    ...partial,
    recordCount,
    recordLimit:            recordCount,
    recordOffset:           0,
    records: range( 0, recordCount )
        .map( i => createRecord({
            prefix:         prefix || partial.id,
            fieldNames:     partial.fieldNames,
        })),
});


export const createDataFile = ({
    fieldCount =            3,
    fileName =              'tests-data-data-file-key.csv',
    recordCount =           3,
}) => createDataFileFull(
    createDataFilePartial({ fieldCount, fileName }),
    { recordCount },
);


export const createDataFileResponse = arg => ({
    getDataFile:            createDataFile( arg ),
});


export const createFullResponse = ( partial, options ) => ({
    getDataFile: createDataFileFull( partial, options ),
});


export default createDataFileResponse({});
