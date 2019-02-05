import { h }            from 'preact';
import { props }        from 'ramda';

import Error            from '../ui-messages/Error';
import Loading          from '../ui-messages/Loading';


export default ({
    className,
    listStatus,
    planStatuses,
    uids,
}) => {
    const statuses =    props( uids, planStatuses );

    const isError = (
        listStatus.addCheckError
        || listStatus.getListError
    );

    const isLoading = (
        listStatus.getListLoading
        || statuses.find( status => (
            status.createLoading
            || status.readLoading
            || status.updateLoading
            || status.deleteLoading
        ))
    );

    return (
        <div className={ className }>{
            isError
                ? <Error justIcon message="There are some errors." />
            : isLoading
                ? <Loading justIcon message="Syncing with server." />
                : <span>✅</span>
        }</div>
    );
};
