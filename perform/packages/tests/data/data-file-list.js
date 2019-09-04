import { range }            from 'ramda';

import {
    createDataFilePartial,
}                           from './data-file';


export const createDataFileList = totalCount => ({
    listDataFiles: {
        __typename:     'DataFiles',
        offset:         0,
        limit:          totalCount,
        totalCount,
        dataFiles: range( 0, totalCount ).map( i =>
            createDataFilePartial({
                fieldCount:     i + 3,
                fileName:       `tests-data-data-file-list-dataFile-${ i }.csv`,
            })
        ),
    },
});


export const EMPTY_DATA_FILE_LIST = createDataFileList( 0 );


export default createDataFileList( 5 );
