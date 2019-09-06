import { h, Component }     from 'preact';
/// import { props }            from 'ramda';

import { composeQueries  }  from '../graphql';
import { concepts }         from '../graphql/queries.graphql';
import DocumentPlansContext from '../document-plans/Context';
import Error                from '../ui-messages/Error';
import Loading              from '../ui-messages/Loading';
import { useStores }        from '../vesa/';


export default composeQueries({
    concepts,
})( useStores([
    'variantsApi',
])( class Status extends Component {

    static contextType =    DocumentPlansContext;

    render({
        className,
        concepts: {
            concepts:       conceptsData,
            error:          conceptsError,
            loading:        conceptsLoading,
        },
        variantsApi,
    }, _, {
        documentPlansError,
        documentPlansLoading,
        openedDataFileError,
        openedDataFileLoading,
        openedPlanError,
        openedPlanLoading,
    }) {
        const isError = (
            conceptsError
            || ( ! conceptsData && ! conceptsLoading )
            || documentPlansError
            || openedDataFileError
            || openedPlanError
            || variantsApi.error
        );

        const isLoading = (
            conceptsLoading
            || documentPlansLoading
            || openedDataFileLoading
            || openedPlanLoading
            || variantsApi.loading
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

        const isReady = !isError && !isLoading;

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
    }
}));
