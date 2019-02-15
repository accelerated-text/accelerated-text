import pTap                 from 'p-tap';

import { getOpenedPlan }    from '../plan-list/functions';

import { getVariants }      from './api';


const updateVariants = ( plan, { E, getStoreState }) => (
    ( getStoreState( 'planList' ).openedPlanUid === plan.uid )
        && E.variantsApi.onGet.async()
);

const getPending = ( _, { E, getStoreState }) => (
    getStoreState( 'variantsApi' ).pending
        && E.variantsApi.onGetStart.async()
);

export default {

    documentPlans: {

        onCreateResult:             updateVariants,
        onUpdateResult:             updateVariants,
        onMergeFromServerResult:    updateVariants,
    },

    planList: {

        onSelectPlan: ( _, { E }) =>
            E.variantsApi.onGet.async(),

        onGetListResult: ( _, { E, getStoreState }) => {
            const {
                error,
                loading,
                pending,
                result,
            } = getStoreState( 'variantsApi' );

            /// Load variants on first document plan load:
            if( !result && !pending && !loading && !error ) {
                E.variantsApi.onGetStart.async();
            }
        },
    },

    variantsApi: {

        onGet: ( _,  { E, getStoreState }) => (
            !getStoreState( 'variantsApi' ).pending
                && E.variantsApi.onGetStart.async()
        ),

        onGetStart: ( _, { E, getStoreState }) => {

            const plan =    getOpenedPlan( getStoreState );
            if( !plan || !plan.id ) {
                return;
            }

            getVariants( plan.id )
                .then( pTap( console.log ))
                .then( result => {
                    E.variantsApi.onGetResult( result );
                    if( getStoreState( 'variantsApi' ).pending ) {
                        E.variantsApi.onGetStart();
                    }
                })
                .catch( pTap.catch( console.error ))
                .catch( E.variantsApi.onGetError );
        },

        onGetError:                 getPending,
        onGetResult:                getPending,
    },
};
