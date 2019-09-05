import { h, Component }     from 'preact';

import { composeQueries }   from '../graphql/';
import { documentPlans }    from '../graphql/queries.graphql';
import DocumentPlansContext from '../document-plans/Context';
import {
    Error,
    Info,
    Loading,
}                           from '../ui-messages/';
import { useStores }        from '../vesa/';


export default useStores([
    'variantsApi',
])( composeQueries({
    documentPlans,
})( class VariantsViewWithStatus extends Component {

    static contextType =    DocumentPlansContext;

    render({
        children,
        className,
        documentPlans: {
            error:              documentPlansError,
        },
        emptyMessage =          'No variants.',
        loadingMessage =        'Loading variants...',
        noDataMessage =         'No data file selected.',
        noPlanMessage =         'Missing document plan.',
        variantsApi,
    }, _, {
        openedPlan,
        openedPlanStatus,
    }) {
        const { result } =      variantsApi;

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
    }
}));
