import pTap             from 'p-tap';

import {
    DELETE,
    GET,
    POST,
    PUT,
}   from './document-plans-api';


const isSamePlan = ( p1, p2 ) => (
    p1.uid === p2.uid
    && p1.id === p2.id
    && p1.updateCount === p2.updateCount
    && p1.name === p2.name
    && p1.blocklyXml === p2.blocklyXml
    && JSON.stringify( p1.documentPlan ) === JSON.stringify( p2.documentPlan )
);


export default {

    plans: {

        onCreate: ( _, { E, getStoreState }) =>

            E.plans.onCreateStart.async(
                getStoreState( 'plans' ).createdPlan
            ),

        onCreateStart: ( plan, { E }) =>

            POST( '/', {
                ...plan,
                createdAt:  undefined,
                id:         undefined,
            })
                .then( E.plans.onCreateResult )
                .catch( pTap( console.error ))
                .catch( createError => E.plans.onCreateError({ createError, plan }))
                .then( E.plans.onCheckPending( plan )),

        onDelete: ( plan, { E, getStoreState }) => {

            const status =  getStoreState( 'plans' ).statuses[plan.uid];

            if( plan.id && !status.deletePending ) {
                E.plans.onDeleteStart.async( plan );
            }
        },

        onDeleteStart: ( plan, { E }) =>

            DELETE( `/${ plan.id }` )
                .then( E.plans.onDeleteResult )
                .catch( pTap( console.error ))
                .catch( deleteError => E.plans.onDeleteError({ deleteError, plan })),

        onRead: ( plan, { E, getStoreState }) => {

            const {
                createLoading,
                deleteLoading,
                deletePending,
                readLoading,
                updateLoading,
                updatePending,
            } = getStoreState( 'plans' ).statuses[plan.uid];

            const isLoading = (
                createLoading
                || deleteLoading
                || readLoading
                || updateLoading
            );

            const isPending = ( deletePending || updatePending );

            if( plan.id && !isLoading && !isPending ) {
                E.plans.onReadStart.async( plan );
            }
        },

        onReadStart: ( plan, { E }) =>

            GET( `/${ plan.id }` )
                .then( E.plans.onReadResult )
                .catch( pTap( console.error ))
                .catch( readError => E.plans.onReadError({ readError, plan })),

        onUpdate: ( plan, { E, getStoreState }) => {

            const {
                isDeleted,
                deleteLoading,
                deletePending,
                updatePending,
            } = getStoreState( 'plans' ).statuses[plan.uid];

            const shouldSkip = (
                isDeleted
                || deleteLoading
                || deletePending
                || updatePending
            );

            if( plan.id && !shouldSkip ) {
                E.plans.onUpdateStart.async( plan );
            }
        },

        onUpdateStart: ( plan, { E, getStoreState }) =>

            PUT( `/${ plan.id }` )
                .then( serverPlan => {
                    const status =  getStoreState( 'plans' ).statuses[plan.uid];

                    if( status.updatePending ) {
                        return;
                    } else if( isSamePlan( serverPlan, plan )) {
                        E.plans.onUpdateResult( serverPlan );
                    } else {
                        E.plans.onDifferingUpdateResult( serverPlan );
                    }
                })
                .catch( pTap( console.error ))
                .catch( updateError => E.plans.onUpdateError({ updateError, plan }))
                .then( E.plans.onCheckPending( plan )),

        onCheckPending: ( plan, { E, getStoreState }) => {

            const {
                plans,
                statuses,
            } = getStoreState( 'plans' );

            const currentPlan = plans[plan.uid];
            const status =      statuses[plan.uid];

            if( status.deletePending ) {
                E.plans.onDeleteStart.async( currentPlan );
            } else if( status.updatePending ) {
                E.plans.onUpdateStart.async( currentPlan );
            }
        },
    },
};
