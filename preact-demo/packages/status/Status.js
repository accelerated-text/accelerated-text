import { h }            from 'preact';
import { props }        from 'ramda';

import Error            from '../ui-messages/Error';
import Loading          from '../ui-messages/Loading';
import { useStores }    from '../vesa/';


export default useStores([
    'documentPlans',
    'planList',
    'variantsApi',
])(({
    className,
    documentPlans,
    planList,
    variantsApi,
}) => {
    const planStatuses =    props( planList.uids, documentPlans.statuses );

    const isError = (
        variantsApi.error
        || planList.addCheckError
        || planList.getListError
    );

    const isLoading = (
        variantsApi.loading
        || planList.getListLoading
        || planStatuses.find( status => (
            status.createLoading
            || status.readLoading
            || status.updateLoading
            || status.deleteLoading
        ))
    );

    return (
        <div className={ className }>{
            isError
                ? <Error message="There are some errors." />
            : isLoading
                ? <Loading message="Syncing..." />
                : <span>✅</span>
        }</div>
    );
});
