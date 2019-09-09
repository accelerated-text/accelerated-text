import { h }                from 'preact';

import composeContexts      from '../compose-contexts/';
import DocumentPlansContext from '../document-plans/Context';
import {
    Error,
    Info,
    Loading,
}                           from '../ui-messages/';

import VariantsContext      from './Context';


export default composeContexts({
    documentPlans:          DocumentPlansContext,
    variants:               VariantsContext,
})(({
    children,
    className,
    documentPlans: {
        openedPlan,
        openedPlanError,
        openedPlanLoading,
    },
    emptyMessage =          'No variants.',
    loadingMessage =        'Loading variants...',
    noDataMessage =         'No data file selected.',
    noPlanMessage =         'Missing document plan.',
    variants,
}) => {
    const { result } =      variants;
    const error =           variants.error || openedPlanError;
    const loading =         variants.loading || openedPlanLoading;

    /* TODO: get full openedPlan status
    const error = (
        variantsApi.error
        || ( openedPlanStatus
            ? ( openedPlanStatus.createError
               || openedPlanStatus.readError
               || openedPlanStatus.updateError
            )
            : documentPlansError
        )
    );
    const loading = (
        openedPlan && (
            ! openedPlanStatus
            || variantsApi.loading
            || openedPlanStatus.createLoading
            || openedPlanStatus.readLoading
            || openedPlanStatus.updateLoading
        )
    );
    */

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
