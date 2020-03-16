import { h }                from 'preact';

import composeContexts      from '../compose-contexts/';
import DocumentPlansContext from '../document-plans/Context';
import {
    Error,
    Info,
    Loading,
}                           from '../ui-messages/';
import OpenedPlanContext    from '../accelerated-text/OpenedPlanContext';

import VariantsContext      from './Context';


export default composeContexts({
    documentPlans:          DocumentPlansContext,
    openedPlan:             OpenedPlanContext,
    variants:               VariantsContext,
})(({
    children,
    className,
    documentPlans,
    openedPlan,
    emptyMessage =          'No variants.',
    loadingMessage =        'Loading variants...',
    noDataMessage =         'No data file selected.',
    noPlanMessage =         'Missing document plan.',
    variants,
}) => {
    const { result } =      variants;
    const error =           variants.error || openedPlan.error || documentPlans.error;
    const loading =         variants.loading || openedPlan.loading;

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
                    openedPlan.plan
                        ? emptyMessage
                        : noPlanMessage
                } />
            }
        </div>
    );
});
