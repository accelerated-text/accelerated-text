import { range }        from 'ramda';


export const createDataFileData = ({
    prefix =    'tests-data-data-file-data',
    rowCount =  3,
}) => ({
    key:    `${ prefix }-key`,
    data:   range( 0, rowCount ).map( row => ({
        First:  `${ prefix } row ${ row } First`,
        Second: `${ prefix } row ${ row } Second`,
        Third:  `${ prefix } row ${ row } Third`,
    })),
});


export default createDataFileData({});
