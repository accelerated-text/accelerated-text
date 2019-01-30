import { h }            from 'preact';

import Error            from '../ui-messages/Error';
import Loading          from '../ui-messages/Loading';
import { useStores }    from '../vesa/';


export default useStores([
    'planList',
])(({
    planList: {
        addCheckError,
        getListError,
        getListLoading,
        statuses,
    },
}) => {

    const isError = (
        addCheckError
        || getListError
    );

    const isLoading = (
        getListLoading
        || Object.values( statuses ).find( status => (
            status.addLoading
            || status.removeLoading
            || status.renameLoading
        ))
    );

    return (
        <div>{
            isError
                ? <Error justIcon message="There are some errors." />
            : isLoading
                ? <Loading justIcon message="Syncing with server." />
                : <span>✅</span>
        }</div>
    );
});
