import { h }                from 'preact';

import {
    Error,
    Info,
    Loading,
}                           from '../ui-messages/';
import getOpenedPlan        from '../plan-list/get-opened-plan';
import { getStatus }        from '../document-plans/functions';
import { useStores }        from '../vesa/';


export default useStores([
    'documentPlans',
    'planList',
    'variantsApi',
])(({
    children,
    className,
    documentPlans,
    emptyMessage =          'No variants.',
    loadingMessage =        'Loading variants...',
    noDataMessage =         'No data file selected.',
    noPlanMessage =         'Missing document plan.',
    planList,
    variantsApi,
}) => {
    const { result } =      variantsApi;
    const openedPlan =      getOpenedPlan({ documentPlans, planList });
    const planStatus =
        openedPlan
            ? getStatus( documentPlans, openedPlan )
            : null;

    const error = (
        variantsApi.error
        || ( planStatus
            ? ( planStatus.createError
               || planStatus.readError
               || planStatus.updateError
            )
            : planList.getListError
        )
    );

    const loading = (
        openedPlan && (
            ! planStatus
            || variantsApi.loading
            || planStatus.createLoading
            || planStatus.readLoading
            || planStatus.updateLoading
        )
    );

    return (
        <div className={ className }>
            { error
                ? <Error message={ error } />
            : loading
                ? <Loading message={ loadingMessage } />
            : ( result && result.variants && result.variants.length )
                ? children({
                    variants:   result.variants,
                })
                : <Info message={
                    openedPlan
                        ? ( openedPlan.dataSampleId
                            ? emptyMessage
                            : noDataMessage
                        )
                        : noPlanMessage
                } />
            }
        </div>
    );
});
