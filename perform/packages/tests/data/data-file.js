import USER         from './user';


export const createDataFile = ({
    fileName =      'tests-data-data-file-key.csv',
}) => ({
    key:            `${ USER.id }/${ fileName }`,
    fieldNames:     [ 'First', 'Second', 'Third' ],
});

export default createDataFile({});
