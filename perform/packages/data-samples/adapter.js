import debugSan             from '../debug-san/';
import { getOpenedPlan }    from '../plan-list/functions';

import { getData, getList } from './api';
import { getStatus }        from './functions';


const debug =               debugSan( 'data-samples/adapter' );

const getDataIfNeeded = ( _, { E, getStoreState }) => {

    const { fileItems } =   getStoreState( 'dataSamples' );
    const openedPlan =      getOpenedPlan( getStoreState );

    const needsData = (
        openedPlan
        && openedPlan.dataSampleId
        && fileItems[openedPlan.dataSampleId]
        && !fileItems[openedPlan.dataSampleId].data
    );

    if( needsData ) {
        E.dataSamples.onGetData.async( fileItems[openedPlan.dataSampleId]);
    }
};


export default {

    componentDidMount: ( _, { E }) => {

        E.dataSamples.onGetList();
    },

    dataSamples: {

        /// Items

        onGetData: ( fileItem, { E, getStoreState }) => (
            !getStatus(
                getStoreState( 'dataSamples' ),
                fileItem,
            ).getDataLoading
                && E.dataSamples.onGetDataStart.async( fileItem )
        ),

        onGetDataStart: ( fileItem, { E }) =>

            getData( fileItem )
                .then( debug.tapThen( 'onGetDataStart' ))
                .then( result => E.dataSamples.onGetDataResult({
                    data:           result.data,
                    fileItem,
                }))
                .catch( debug.tapCatch( 'onGetDataStart' ))
                .catch( getDataError => E.dataSamples.onGetDataError({ fileItem, getDataError })),

        /// List

        onGetList: ( _, { E, getStoreState }) =>

            !getStoreState( 'dataSamples' ).getListLoading &&
                E.dataSamples.onGetListStart.async(),

        onGetListStart: ( _, { E, getStoreState }) =>

            getList( getStoreState( 'user' ).id )
                .then( debug.tapThen( 'onGetListStart' ))
                .then( E.dataSamples.onGetListResult )
                .catch( debug.tapCatch( 'onGetListStart' ))
                .catch( E.dataSamples.onGetListError ),

        onGetListResult:            getDataIfNeeded,
    },

    documentPlans: {
        onCreateResult:             getDataIfNeeded,
        onUpdateResult:             getDataIfNeeded,
        onMergeFromServerResult:    getDataIfNeeded,
    },

    planList: {
        onGetListResult:            getDataIfNeeded,
        onSelectPlan:               getDataIfNeeded,
    },
};
