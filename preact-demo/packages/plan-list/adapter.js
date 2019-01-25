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
                addLoading,
                addNewPlan,
            } = getStoreState( 'planList' );

            if( addLoading || !addNewPlan ) {
                return;
            }

            E.planList.onAddStart.async( addNewPlan.uid );

            POST( '/', {
                ...addNewPlan,
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
                removeLoading,
            } = getStoreState( 'planList' );

            if( removeLoading || !item ) {
                return;
            }

            E.planList.onRemoveStart.async( item.uid );

            DELETE( `/${ item.id }` )
                .then( E.planList.onRemoveResult )
                .catch( pTap( console.error ))
                .catch( E.planList.onRemoveError )
                .then( E.planList.onGetList );
        },

        onRenamePlan: ({ item, name }, { E, getStoreState }) => {

            const {
                renameLoading,
            } = getStoreState( 'planList' );

            if( renameLoading || !item || !name ) {
                return;
            }

            E.planList.onRenameStart.async( item.uid );

            PUT( `/${ item.id }`, {
                ...item,
                name,
            })
                .then( E.planList.onRenameResult )
                .catch( pTap( console.error ))
                .catch( E.planList.onRenameError )
                .then( E.planList.onGetList );
        },
    },
};
