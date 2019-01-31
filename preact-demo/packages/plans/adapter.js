import pTap             from 'p-tap';

import {
    DELETE,
    GET,
    POST,
    PUT,
}   from './document-plans-api';


const doDeletePlan = ( plan, { E }) => {

    E.plans.onDeleteStart.async( plan );
    
    DELETE( `/${ plan.id }` )
        .then( E.plans.onDeleteResult )
        .catch( pTap( console.error ))
        .catch( deleteError => E.plans.onDeleteError({ deleteError, plan }));
};

const doUpdatePlan = ( plan, { E, getStoreState }) => {
    E.plans.onUpdateStart.async( plan );

    PUT( `/${ plan.id }` )
        .then( serverPlan => {
            const status =  getStoreState( 'plans' ).statuses[plan.uid];

            if( status.updatePending ) {
                return;
            } else if( serverPlan.updateCount !== plan.updateCount ) {
                E.plans.onDifferingUpdateResult( serverPlan );
            } else {
                E.plans.onUpdateResult( serverPlan );
            }
        })
        .catch( pTap( console.error ))
        .catch( updateError => E.plans.onUpdateError({ updateError, plan }))
        .then( E.plans.onCheckPending( plan ));
};


export default {

    plans: {

        onCreate: ( _, { E, getStoreState }) => {

            const { createdPlan } = getStoreState( 'plans' );

            E.plans.onCreateStart.async( createdPlan );

            POST( '/', {
                ...createdPlan,
                createdAt:  undefined,
                id:         undefined,
            })
                .then( E.plans.onCreateResult )
                .catch( pTap( console.error ))
                .catch( createError => E.plans.onCreateError({ createError, createdPlan }))
                .then( E.plans.onCheckPending( createdPlan ));
        },

        onDelete: ( plan, { E, getStoreState }) => {

            const {
                deletePending,
            } = getStoreState( 'plans' ).statuses[plan.uid];

            if( deletePending || !plan.id ) {
                return;
            } else {
                doDeletePlan( plan, { E });
            }
        },

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

            if( isLoading || isPending || !plan.id ) {
                return;
            }

            GET( `/${ plan.id }` )
                .then( E.plans.onReadResult )
                .catch( pTap( console.error ))
                .catch( readError => E.plans.onReadError({ readError, plan }));
        },

        onUpdate: ( plan, { E, getStoreState }) => {

            const {
                isDeleted,
                deleteLoading,
                deletePending,
                updatePending,
            } = getStoreState( 'plans' ).statuses[plan.uid];

            const shouldSkip = (
                !plan.id
                || isDeleted
                || deleteLoading
                || deletePending
                || updatePending
            );

            if( shouldSkip ) {
                return;
            } else {
                doUpdatePlan( plan, { E, getStoreState });
            }
        },

        onCheckPending: ( plan, { E, getStoreState }) => {

            const {
                plans,
                statuses,
            } = getStoreState( 'plans' );

            const currentPlan = plans[plan.uid];
            const status =      statuses[plan.uid];

            if( status.deletePending ) {
                doDeletePlan( currentPlan, { E });
            } else if( status.updatePending ) {
                doUpdatePlan( currentPlan, { E, getStoreState });
            }
        },
    },
};
