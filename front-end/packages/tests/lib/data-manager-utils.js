import { respondOnPlanChange }  from './responses';
import { SELECTORS }            from '../constants';


export const selectDataFile = ( t, dataFile, documentPlan = null ) =>
    Promise.all([
        t.graphqlApi.provideOnce(
            'getRelevantSamples',
            { id: dataFile.id },
            { data: { getDataFile: dataFile }},
        ),
        respondOnPlanChange( t, {
            ...documentPlan,
            dataSampleId:       dataFile.id,
            dataSampleRow:      0,
        }),
        t.page.select( SELECTORS.DATA_MANAGER_FILE_LIST, dataFile.id ),
    ]);


export const isDataFileRecordVisible = async ( t, record ) => {

    for( let i = 0; i < record.fields.length; i += 1 ) {
        const field =       record.fields[i];

        t.is(
            await t.getElementText(
                `tr:nth-child(${ i + 1 }) > ${ SELECTORS.DATA_MANAGER_CELL_NAME }`
            ),
            field.fieldName,
        );
        t.is(
            await t.getElementText(
                `tr:nth-child(${ i + 1 }) > ${ SELECTORS.DATA_MANAGER_CELL_VALUE }`
            ),
            field.value,
        );
    }
};
