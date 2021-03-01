import { h }                from 'preact';

import composeContexts      from '../compose-contexts/';
import { composeQueries  }  from '../graphql';
import { concepts }         from '../graphql/queries.graphql';
import DocumentPlansContext from '../document-plans/Context';
import Error                from '../ui-messages/Error';
import Loading              from '../ui-messages/Loading';
import OpenedPlanContext    from '../amr/OpenedPlanContext';


export default composeContexts({
    documentPlans:          DocumentPlansContext,
    openedPlan:             OpenedPlanContext,
})( composeQueries({
    concepts,
})(({
    className,
    concepts,
    documentPlans,
    openedPlan,
}) => {
    const isError = (
        concepts.error
        || ( ! concepts.concepts && ! concepts.loading )
        || documentPlans.error
        || openedPlan.error
    );

    const isLoading = (
        concepts.loading
        || documentPlans.loading
        || openedPlan.loading
    );
    /// TODO: https://gitlab.com/tokenmill/nlg/accelerated-text/issues/272
    /// TODO: https://gitlab.com/tokenmill/nlg/accelerated-text/issues/273

    const isReady = ! isError && ! isLoading;

    return (
        <div className={ className }>
            { isLoading &&
                <Loading justIcon message="Syncing... " />
            }
            { isError &&
                <Error justIcon message="There are some errors." />
            }
            { isReady &&
                <span>✅</span>
            }
        </div>
    );
}));
