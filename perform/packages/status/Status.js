import { h }                from 'preact';

import composeContexts      from '../compose-contexts/';
import { composeQueries  }  from '../graphql';
import { concepts }         from '../graphql/queries.graphql';
import DocumentPlansContext from '../document-plans/Context';
import Error                from '../ui-messages/Error';
import Loading              from '../ui-messages/Loading';
import OpenedFileContext    from '../accelerated-text/OpenedDataFileContext';
import OpenedPlanContext    from '../accelerated-text/OpenedPlanContext';
import VariantsContext      from '../variants/Context';


export default composeContexts({
    documentPlans:          DocumentPlansContext,
    openedDataFile:         OpenedFileContext,
    openedPlan:             OpenedPlanContext,
    variants:               VariantsContext,
})( composeQueries({
    concepts,
})(({
    className,
    concepts,
    documentPlans,
    openedDataFile,
    openedPlan,
    variants,
}) => {
    const isError = (
        concepts.error
        || ( ! concepts.concepts && ! concepts.loading )
        || documentPlans.error
        || openedDataFile.error
        || openedPlan.error
        || variants.error
    );

    const isLoading = (
        concepts.loading
        || documentPlans.loading
        || openedDataFile.loading
        || openedPlan.loading
        || variants.loading
    );
    /// TODO: add upload status
    /// TODO: add all plans statuses

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
