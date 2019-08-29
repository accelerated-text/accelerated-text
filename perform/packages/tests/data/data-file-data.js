import { range, zipObj }    from 'ramda';


export const createDataFileData = ({
    fieldNames =    [ 'One', 'Two', 'Three' ],
    prefix =        'tests-data-data-file-data',
    rowCount =      3,
}) => ({
    id:    `${ prefix }-key`,
    data:   range( 0, rowCount ).map( i =>
        zipObj(
            fieldNames,
            fieldNames.map( fieldName => `${ prefix } ${ fieldName } value` ),
        )),
});


export default createDataFileData({});
