import { h }                from 'preact';

import { getPlanDataRow }   from '../data-samples/functions';
import getResultForRow      from '../variants/get-result-for-row';
import {
    Info,
    Loading,
}                           from '../ui-messages/';
import { useStores }        from '../vesa/';

import { GraphQLProvider }  from './apollo-client';
import Publisher            from './Publisher';


export default useStores([
    'variantsApi',
])(({
    fileItem,
    plan,
    variantsApi: { error, loading, result },
}) => {
    const descriptionText = getResultForRow(
        result,
        plan && plan.dataSampleRow,
    );
    const record =          getPlanDataRow( fileItem, plan );

    return (
        <GraphQLProvider>
            { ! plan
                ? <Loading message="Waiting for document plan." />
            : ! plan.dataSampleId
                ? <Info message="No data file selected." />
            : record
                ? <Publisher
                    descriptionText={ descriptionText }
                    query={ `sku:${ record['isbn-13'] }` }
                    record={ record }
                />
                : <Loading message="Loading file data." />
            }
        </GraphQLProvider>
    );
});
