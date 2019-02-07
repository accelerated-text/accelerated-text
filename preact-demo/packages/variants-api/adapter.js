import pTap                 from 'p-tap';

import { getOpenedPlan }    from '../plan-list/functions';
import variantsApi          from '../variants-api/';


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

            variantsApi.getForDataSample({
                dataSampleId:   'TODO_REPLACE',
                documentPlanId: plan.id,
            })
                .then( E.variantsApi.onGetResult )
                .catch( pTap( console.error ))
                .catch( E.variantsApi.onGetError );
        },

        onGetError:                 getPending,
        onGetResult:                getPending,
    },
};
