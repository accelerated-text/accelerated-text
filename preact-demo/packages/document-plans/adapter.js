import pTap             from 'p-tap';

import {
    DELETE,
    GET,
    POST,
    PUT,
}   from './api';
import {
    getPlan,
    getStatus,
    isSamePlan,
}   from './functions';


const getStoreStatus = ( getStoreState, plan ) =>
    getStatus( getStoreState( 'documentPlans' ), plan );


export default {

    documentPlans: {

        onCreate: ( _, { E, getStoreState }) =>

            E.documentPlans.onCreateStart.async(
                getStoreState( 'documentPlans' ).createdPlan
            ),

        onCreateStart: ( plan, { E }) =>

            POST( '/', {
                ...plan,
                createdAt:  undefined,
                id:         undefined,
            })
                .then( E.documentPlans.onCreateResult )
                .catch( pTap( console.error ))
                .catch( createError => E.documentPlans.onCreateError({ createError, plan }))
                .then(() => E.documentPlans.onCheckPending( plan )),

        onDelete: ( plan, { E, getStoreState }) => {

            const status =  getStoreStatus( getStoreState, plan );

            if( plan.id && !status.deletePending ) {
                E.documentPlans.onDeleteStart.async( plan );
            }
        },

        onDeleteStart: ( plan, { E }) =>

            DELETE( `/${ plan.id }` )
                .then( E.documentPlans.onDeleteResult )
                .catch( pTap( console.error ))
                .catch( deleteError => E.documentPlans.onDeleteError({ deleteError, plan })),

        onRead: ( plan, { E, getStoreState }) => {

            const {
                createLoading,
                deleteLoading,
                deletePending,
                readLoading,
                updateLoading,
                updatePending,
            } = getStoreStatus( getStoreState, plan );

            const isLoading = (
                createLoading
                || deleteLoading
                || readLoading
                || updateLoading
            );

            const isPending = ( deletePending || updatePending );

            if( plan.id && !isLoading && !isPending ) {
                E.documentPlans.onReadStart.async( plan );
            }
        },

        onReadStart: ( plan, { E }) =>

            GET( `/${ plan.id }` )
                .then( E.documentPlans.onReadResult )
                .catch( pTap( console.error ))
                .catch( readError => E.documentPlans.onReadError({ readError, plan })),

        onUpdate: ( plan, { E, getStoreState }) => {

            const {
                isDeleted,
                deleteLoading,
                deletePending,
                updatePending,
            } = getStoreStatus( getStoreState, plan );

            const shouldSkip = (
                isDeleted
                || deleteLoading
                || deletePending
                || updatePending
            );

            if( plan.id && !shouldSkip ) {
                E.documentPlans.onUpdateStart.async( plan );
            }
        },

        onUpdateStart: ( plan, { E, getStoreState }) =>

            PUT( `/${ plan.id }` )
                .then( serverPlan => {
                    const status =  getStoreStatus( getStoreState, plan );

                    if( status.updatePending ) {
                        return;
                    } else if( isSamePlan( serverPlan, plan )) {
                        E.documentPlans.onUpdateResult( serverPlan );
                    } else {
                        E.documentPlans.onDifferingUpdateResult( serverPlan );
                    }
                })
                .catch( pTap( console.error ))
                .catch( updateError => E.documentPlans.onUpdateError({ updateError, plan }))
                .then(() => E.documentPlans.onCheckPending( plan )),

        onCheckPending: ( plan, { E, getStoreState }) => {

            const state =       getStoreState( 'documentPlans' );
            const currentPlan = getPlan( state, plan );
            const status =      getStatus( state, plan );

            if( status.deletePending ) {
                E.documentPlans.onDeleteStart.async( currentPlan );
            } else if( status.updatePending ) {
                E.documentPlans.onUpdateStart.async( currentPlan );
            }
        },
    },
};
