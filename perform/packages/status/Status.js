import { h }            from 'preact';
import { props }        from 'ramda';

import Error            from '../ui-messages/Error';
import Loading          from '../ui-messages/Loading';
import { useStores }    from '../vesa/';


export default useStores([
    'contexts',
    'dataSamples',
    'documentPlans',
    'planList',
    'variantsApi',
])(({
    className,
    contexts,
    dataSamples,
    documentPlans,
    planList,
    variantsApi,
}) => {
    const dataStatuses =    props( dataSamples.fileIds, dataSamples.statuses );
    const planStatuses =    props( planList.uids, documentPlans.statuses );

    const isError = (
        variantsApi.error
        || contexts.getListError
        || dataSamples.getListError
        || planList.addCheckError
        || planList.getListError
    );

    const isLoading = (
        variantsApi.loading
        || contexts.getListLoading
        || dataSamples.getListLoading
        || planList.getListLoading
        || dataStatuses.find( status => status.getDataLoading )
        || planStatuses.find( status => (
            status.createLoading
            || status.readLoading
            || status.updateLoading
            || status.deleteLoading
        ))
    );

    const isReady = !isError && !isLoading;

    return (
        <div className={ className }>
            { isLoading &&
                <Loading message="Syncing... " />
            }
            { isError &&
                <Error justIcon message="There are some errors." />
            }
            { isReady &&
                <span>✅</span>
            }
        </div>
    );
});
