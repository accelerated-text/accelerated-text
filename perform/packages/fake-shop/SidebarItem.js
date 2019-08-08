import { h }                from 'preact';
import { path }             from 'ramda';

import { useStores }        from '../vesa/';

import Publisher            from './Publisher';


export default useStores([
    'variantsApi',
])(({
    fileItem,
    plan,
    variantsApi: { result },
}) =>
    <Publisher
        record={
            fileItem
            && fileItem.data
            && plan
            && fileItem.data[ plan.dataSampleRow || 0 ]
        }
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
    />
);
