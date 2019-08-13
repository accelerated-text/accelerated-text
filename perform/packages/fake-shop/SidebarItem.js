import checkPropTypes       from 'check-prop-types';
import { h }                from 'preact';

import { getPlanDataRow }   from '../data-samples/functions';
import getResultForRow      from '../variants/get-result-for-row';
import {
    Error,
    Info,
    Loading,
}                           from '../ui-messages/';
import { useStores }        from '../vesa/';

import BookRecord           from './BookRecord.type';
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

    const isValidRecord = (
        record
        && ! checkPropTypes({ record: BookRecord }, { record })
    );

    return (
        <GraphQLProvider>
            { ! plan
                ? <Loading message="Waiting for document plan." />
            : ! plan.dataSampleId
                ? <Info message="No data file selected." />
            : ! record
                ? <Loading message="Loading file data." />
            : ! isValidRecord
                ? <Error message="Unsupported data from data file." />
                : <Publisher
                    descriptionText={ descriptionText }
                    query={ `sku:${ record['isbn-13'] }` }
                    record={ record }
                />
            }
        </GraphQLProvider>
    );
});
