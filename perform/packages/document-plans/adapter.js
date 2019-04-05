import debugSan         from '../debug-san/';

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


const debug =           debugSan( 'document-plans/adapter' );

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
                .then( debug.tapThen( 'onCreateStart' ))
                .then( E.documentPlans.onCreateResult )
                .catch( debug.tapCatch( 'onCreateStart' ))
                .catch( createError => E.documentPlans.onCreateError({ createError, plan }))
                .then(() => E.documentPlans.onCheckPending( plan )),

        onDelete: ( plan, { E, getStoreState }) => {

            const status =  getStoreStatus( getStoreState, plan );

            debug( 'onDelete', plan, { status });
            if( plan.id && !status.deletePending ) {
                E.documentPlans.onDeleteStart.async( plan );
            }
        },

        onDeleteStart: ( plan, { E }) =>

            DELETE( `/${ plan.id }` )
                .then( debug.tapThen( 'onDeleteStart' ))
                .then( E.documentPlans.onDeleteResult )
                .catch( debug.tapCatch( 'onDeleteStart' ))
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

            debug( 'onRead', { isLoading, isPending });

            if( plan.id && !isLoading && !isPending ) {
                E.documentPlans.onReadStart.async( plan );
            }
        },

        onReadStart: ( plan, { E }) =>

            GET( `/${ plan.id }` )
                .then( debug.tapThen( 'onReadStart' ))
                .then( E.documentPlans.onReadResult )
                .catch( debug.tapCatch( 'onReadStart' ))
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

            debug( 'onUpdate', plan, { shouldSkip });
            if( plan.id && !shouldSkip ) {
                E.documentPlans.onUpdateStart.async( plan );
            }
        },

        onUpdateStart: ( plan, { E, getStoreState }) =>

            PUT( `/${ plan.id }`, plan )
                .then( serverPlan => {
                    const status =  getStoreStatus( getStoreState, plan );
                    debug( 'onUpdateStart then', plan, serverPlan, { status });

                    if( status.updatePending ) {
                        return;
                    } else if( isSamePlan( serverPlan, plan )) {
                        E.documentPlans.onUpdateResult( serverPlan );
                    } else {
                        E.documentPlans.onDifferingUpdateResult( serverPlan );
                    }
                })
                .catch( debug.tapCatch( 'onUpdateStart' ))
                .catch( updateError => E.documentPlans.onUpdateError({ updateError, plan }))
                .then(() => E.documentPlans.onCheckPending( plan )),

        onCheckPending: ( plan, { E, getStoreState }) => {

            const state =       getStoreState( 'documentPlans' );
            const currentPlan = getPlan( state, plan );
            const status =      getStatus( state, plan );
            debug( 'onCheckPending', plan, { currentPlan, status });

            if( status.deletePending ) {
                E.documentPlans.onDeleteStart.async( currentPlan );
            } else if( status.updatePending ) {
                E.documentPlans.onUpdateStart.async( currentPlan );
            }
        },
    },
};
