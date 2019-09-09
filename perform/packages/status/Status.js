import { h }                from 'preact';
/// import { props }            from 'ramda';

import composeContexts      from '../compose-contexts/';
import { composeQueries  }  from '../graphql';
import { concepts }         from '../graphql/queries.graphql';
import DocumentPlansContext from '../document-plans/Context';
import Error                from '../ui-messages/Error';
import Loading              from '../ui-messages/Loading';
import VariantsContext      from '../variants/Context';


export default composeContexts({
    documentPlans:          DocumentPlansContext,
    variants:               VariantsContext,
})( composeQueries({
    concepts,
})(({
    className,
    concepts: {
        concepts:       conceptsData,
        error:          conceptsError,
        loading:        conceptsLoading,
    },
    documentPlans: {
        documentPlansError,
        documentPlansLoading,
        openedDataFileError,
        openedDataFileLoading,
        openedPlanError,
        openedPlanLoading,
    },
    variants,
}) => {
    const isError = (
        conceptsError
        || ( ! conceptsData && ! conceptsLoading )
        || documentPlansError
        || openedDataFileError
        || openedPlanError
        || variants.error
    );

    const isLoading = (
        conceptsLoading
        || documentPlansLoading
        || openedDataFileLoading
        || openedPlanLoading
        || variants.loading
    );

    /* TODO: get full statuses
    const planStatuses =    props( planList.uids, documentPlans.statuses );

    const isError = (
        conceptsError
        || ( ! conceptsData && ! conceptsLoading )
        || openedDataFileError
        || planList.addCheckError
        || planList.getListError
        || variantsApi.error
    );

    const isLoading = (
        conceptsLoading
        || variantsApi.loading
        || openedDataFileLoading
        || planList.getListLoading
        || planStatuses.find( status => (
            status.createLoading
            || status.readLoading
            || status.updateLoading
            || status.deleteLoading
        ))
    );
    */

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
