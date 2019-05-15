import { range }        from 'ramda';

import USER         from './user';


export const createDataFile = ({
    fieldCount =    3,
    fileName =      'tests-data-data-file-key.csv',
}) => ({
    key:            `${ USER.id }/${ fileName }`,
    fieldNames:     range( 0, fieldCount ).map( i => `${ fileName } ${ i }` ),
});

export default createDataFile({});
