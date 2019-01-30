import pTap             from 'p-tap';

import {
    DELETE,
    GET,
    POST,
    PUT,
}   from './document-plans-api';


export default {

    componentDidMount: ( _, { E }) => {

        E.planList.onGetList();
    },

    planList: {

        onAddNew: ( name, { E, getStoreState }) => {

            const {
                addedPlan,
            } = getStoreState( 'planList' );

            E.planList.onAddStart.async( addedPlan.uid );

            POST( '/', {
                ...addedPlan,
                createdAt:  undefined,
                id:         undefined,
            })
                .then( E.planList.onAddResult )
                .catch( pTap( console.error ))
                .catch( E.planList.onAddError )
                .then( E.planList.onGetList );
        },

        onGetList: ( _, { E, getStoreState }) => {

            const {
                getListLoading,
            } = getStoreState( 'planList' );

            if( getListLoading ) {
                return;
            }

            E.planList.onGetListStart.async();

            GET( '/' )
                .then( E.planList.onGetListResult )
                .catch( pTap( console.error ))
                .catch( E.planList.onGetListError );
        },

        onRemovePlan: ( item, { E, getStoreState }) => {

            const {
                statuses,
            } = getStoreState( 'planList' );

            if( !item || statuses[item.uid].removeLoading ) {
                return;
            }

            E.planList.onRemoveStart.async( item );

            DELETE( `/${ item.id }` )
                .then( E.planList.onRemoveResult )
                .catch( pTap( console.error ))
                .catch( removeError => E.planList.onRemoveError({ removeError, item }))
                .then( E.planList.onGetList );
        },

        onRenamePlan: ({ item, name }, { E, getStoreState }) => {

            const {
                statuses,
            } = getStoreState( 'planList' );

            if( !item || !name || statuses[item.uid].renameLoading ) {
                return;
            }

            E.planList.onRenameStart.async( item );

            PUT( `/${ item.id }`, {
                ...item,
                name,
            })
                .then( E.planList.onRenameResult )
                .catch( pTap( console.error ))
                .catch( renameError => E.planList.onRenameError({ renameError, item }))
                .then( E.planList.onGetList );
        },
    },
};
