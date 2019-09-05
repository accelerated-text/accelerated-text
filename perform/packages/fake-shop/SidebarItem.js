import checkPropTypes       from 'check-prop-types';
import { h }                from 'preact';

import { composeQueries }   from '../graphql/';
import { documentPlans }    from '../graphql/queries.graphql';
import { getPlanDataRow }   from '../data-samples/functions';
import getResultForRow      from '../variants/get-result-for-row';
import {
    Info,
    Loading,
    Warning,
}                           from '../ui-messages/';
import { useStores }        from '../vesa/';

import BookRecord           from './BookRecord.type';
import { GraphQLProvider }  from './apollo-client';
import Publisher            from './Publisher';


export default composeQueries({
    documentPlans,
})( useStores([
    'variantsApi',
])(({
    dataFile,
    documentPlans: { loading },
    plan,
    variantsApi,
}) => {
    const descriptionText = getResultForRow(
        variantsApi.result,
        plan && plan.dataSampleRow,
    );
    const record =          getPlanDataRow( dataFile, plan );

    const isValidRecord = (
        record
        && ! checkPropTypes({ record: BookRecord }, { record })
    );

    return (
        <GraphQLProvider>
            { ! plan
                ? ( loading
                    ? <Loading message="Waiting for document plan." />
                    : <Info message="Missing document plan." />
                )
            : ! plan.dataSampleId
                ? <Info message="No data file selected." />
            : ! record
                ? <Loading message="Loading file data." />
            : ! isValidRecord
                ? <Warning message="Unsupported data from data file." />
                : <Publisher
                    descriptionText={ descriptionText }
                    query={ `sku:${ record['isbn-13'] }` }
                    record={ record }
                />
            }
        </GraphQLProvider>
    );
}));
