import pTap             from 'p-tap';

import {
    DELETE,
    GET,
    POST,
    PUT,
}   from './document-plans-api';
import {
    findById,
    findByTmpId,
}   from './functions';


export default {

    componentDidMount: ( _, { E }) => {

        E.planList.onGetList();
    },

    planList: {

        onAddNew: ( name, { E, getStoreState }) => {

            const {
                addLoading,
                addTmpId,
                plans,
            } = getStoreState( 'planList' );

            const tmpPlan = findByTmpId( plans, addTmpId );

            if( addLoading || !tmpPlan ) {
                return;
            }

            E.planList.onAddStart.async();

            POST( '/', {
                ...tmpPlan,
                createdAt:  undefined,
                id:         undefined,
                tmpId:      undefined,
            })
                .then( E.planList.onAddResult )
                .catch( pTap( console.error ))
                .catch( E.planList.onAddError )
                .then( E.planList.onGetList );
        },

        onGetList: ( _, { E }) => {

            E.planList.onGetListStart.async();

            GET( '/' )
                .then( E.planList.onGetListResult )
                .catch( pTap( console.error ))
                .catch( E.planList.onGetListError );
        },

        onRemovePlan: ( id, { E, getStoreState }) => {

            const { removeLoading } =   getStoreState( 'planList' );

            if( removeLoading || !id ) {
                return;
            }

            E.planList.onRemoveStart.async( id );

            DELETE( `/${ id }` )
                .then( E.planList.onRemoveResult )
                .catch( pTap( console.error ))
                .catch( E.planList.onRemoveError )
                .then( E.planList.onGetList );
        },

        onRenamePlan: ({ id, name }, { E, getStoreState }) => {

            const {
                plans,
                renameLoading,
            } = getStoreState( 'planList' );

            const plan =    findById( plans, id );

            if( renameLoading || !id || !plan || !name ) {
                return;
            }

            E.planList.onRenameStart.async( id );

            PUT( `/${ id }`, {
                ...plan,
                name,
            })
                .then( E.planList.onRenameResult )
                .catch( pTap( console.error ))
                .catch( E.planList.onRenameError )
                .then( E.planList.onGetList );
        },
    },
};
