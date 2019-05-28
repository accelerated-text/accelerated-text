import { respondOnPlanChange }  from './responses';
import { SELECTORS }            from '../constants';


export const selectDataFile = ( t, fileId, fileData ) => {

    t.page.select( SELECTORS.DATA_MANAGER_FILE_LIST, fileId );

    return Promise.all([
        t.nlgApi.provideOnce( 'GET', `/data/${ fileId }`, fileData ),
        respondOnPlanChange( t ),
    ]);
};


export const isDataFileRowVisible = async ( t, row ) => {

    const rowKeys =         Object.keys( row );

    for( let i = 0; i < rowKeys.length; i += 1 ) {
        const rowKey =      rowKeys[i];

        t.is(
            await t.getElementText(
                `tr:nth-child(${ i + 1 }) > ${ SELECTORS.DATA_MANAGER_CELL_NAME }`
            ),
            rowKey,
        );
        t.is(
            await t.getElementText(
                `tr:nth-child(${ i + 1 }) > ${ SELECTORS.DATA_MANAGER_CELL_VALUE }`
            ),
            row[rowKey],
        );
    }
};
