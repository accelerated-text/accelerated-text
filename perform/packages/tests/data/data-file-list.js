import { range }            from 'ramda';

import { createDataFile }   from './data-file';


export const createDataFileList = totalCount => ({
    listDataFiles: {
        __typename:     'DataFiles',
        offset:         0,
        limit:          totalCount,
        totalCount,
        dataFiles: range( 0, totalCount ).map( i =>
            createDataFile({
                fieldCount:     i + 3,
                fileName:       `tests-data-data-file-list-fileName-${ i }.csv`,
                recordCount:    i + 1,
            })
        ),
    },
});


export const EMPTY_DATA_FILE_LIST = createDataFileList( 0 );


export default createDataFileList( 5 );
