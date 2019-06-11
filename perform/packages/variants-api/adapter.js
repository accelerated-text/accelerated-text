import debugSan             from '../debug-san/';
import { getOpenedPlan }    from '../plan-list/functions';

import { getVariants }      from './api';


const debug =               debugSan( 'variants-api/adapter' );

const updateVariants = ( plan, { E, getStoreState }) => (
    ( getStoreState( 'planList' ).openedPlanUid === plan.uid )
        && E.variantsApi.onGet.async()
);

const getPending = ( _, { E, getStoreState }) => (
    getStoreState( 'variantsApi' ).pending
        && E.variantsApi.onGetStart.async()
);

const updateOnNewOpened = ( _, { E, getStoreState }) => {

    const {
        openedPlanUid,
        previousOpenedPlanUid,
    } = getStoreState( 'planList' );
    const {
        loading,
        result,
    } = getStoreState( 'variantsApi' );
    const plan =    getOpenedPlan( getStoreState );

    const needsVariants = (
        plan && (
            openedPlanUid !== previousOpenedPlanUid
            || ( !loading && !result )
        )
    );

    if( needsVariants ) {
        E.variantsApi.onGet.async();
    }
};


export default {

    documentPlans: {

        onCreateResult:             updateVariants,
        onUpdateResult:             updateVariants,
        onMergeFromServerResult:    updateVariants,
    },

    planList: {

        onSelectPlan:               updateOnNewOpened,
        onGetListResult:            updateOnNewOpened,
    },

    reader: {

        onToggleFlag: ( _, { E }) =>
            E.variantsApi.onGet.async(),
    },

    variantsApi: {

        onGet: ( _,  { E, getStoreState }) => (
            !getStoreState( 'variantsApi' ).pending
                && E.variantsApi.onGetStart.async()
        ),

        onGetStart: ( _, { E, getStoreState }) => {

            const plan =    getOpenedPlan( getStoreState );
            const reader =  getStoreState( 'reader' );

            getVariants({
                ccg:                !!plan.useCcg,
                dataId:             plan.dataSampleId,
                documentPlanId:     plan.id,
                readerFlagValues:   reader.flagValues,
            })
                .then( debug.tapThen( 'getVariants result' ))
                .then( result => {
                    E.variantsApi.onGetResult( result );
                    if( getStoreState( 'variantsApi' ).pending ) {
                        E.variantsApi.onGetStart();
                    }
                })
                .catch( debug.tapCatch( 'getVariants error' ))
                .catch( E.variantsApi.onGetError );
        },

        onGetError:                 getPending,
        onGetResult:                getPending,
    },
};
