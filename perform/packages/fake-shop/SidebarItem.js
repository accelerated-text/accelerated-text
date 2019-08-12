import { h }                from 'preact';

import { getPlanDataRow }   from '../data-samples/functions';
import getResultForRow      from '../variants-api/get-result-for-row';
import { GraphQLProvider }  from './apollo-client';
import { useStores }        from '../vesa/';

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
            { record
                ? <Publisher
                    descriptionError={ error }
                    descriptionLoading={ loading }
                    descriptionText={ descriptionText }
                    query={ `sku:${ record['isbn-13'] }` }
                    record={ record }
                />
                : <div />
            }
        </GraphQLProvider>
    );
});
