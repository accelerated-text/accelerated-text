import { h }                from 'preact';
import { path }             from 'ramda';

import { GraphQLProvider }  from './apollo-client';
import { useStores }        from '../vesa/';

import Publisher            from './Publisher';


export default useStores([
    'variantsApi',
])(({
    fileItem,
    plan,
    variantsApi: { result },
}) => {
    const record = (
        fileItem
        && fileItem.data
        && plan
        && fileItem.data[ plan.dataSampleRow || 0 ]
    );
    const query = (
        record
            ? `sku:${ record['isbn-13'] }`
            : '*'
    );

    return (
        <GraphQLProvider>
            <Publisher
                descriptionText={
                    path(
                        [
                            'variants', 0,
                            'children', 0,
                            'children', plan ? plan.dataSampleRow : 0,
                            'text',
                        ],
                        result,
                    )
                }
                query={ query }
                record={ record }
            />
            { JSON.stringify( record ) }
        </GraphQLProvider>
    );
});
