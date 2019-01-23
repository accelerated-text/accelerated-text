import pTap             from 'p-tap';

import {
    DELETE,
    GET,
    POST,
    PUT,
} from '../plan-api/plan-api';


const NEW_JSON = {
    type:       'Document-plan',
    blocklyId:  'new-document-plan',
    segments: [{
        type:       'Segment',
        blocklyId:  'new-segment',
        text_type:  'description',
        children:   [],
    }],
};

const NEW_XML = '<xml xmlns="http://www.w3.org/1999/xhtml"><block type="Document-plan" deletable="false"><statement name="segments"><block type="Segment"><mutation value_count="2" value_sequence="value_"></mutation><field name="text_type">description</field></block></statement></block></xml>';


export default {

    componentDidMount: ( _, { E }) => {

        E.planSelector.onGetList();
    },

    planSelector: {

        onAddNew: ( name, { E, getStoreState }) => {

            const { addLoading } =  getStoreState( 'planSelector' );

            if( addLoading || !name ) {
                return;
            }

            E.planSelector.onAddStart.async();

            POST( '/', {
                name,
                blocklyXml:     NEW_XML,
                documentPlan:   NEW_JSON,
            })
                .then( E.planSelector.onAddResult )
                .catch( pTap( console.error ))
                .catch( E.planSelector.onAddError );
        },

        onGetList: ( _, { E }) => {

            E.planSelector.onGetListStart.async();

            GET( '/' )
                .then( E.planSelector.onGetListResult )
                .catch( pTap( console.error ))
                .catch( E.planSelector.onGetListError );
        },

        onRemovePlan: ( id, { E, getStoreState }) => {

            const { removeLoading } =   getStoreState( 'planSelector' );

            if( removeLoading || !id ) {
                return;
            }

            E.planSelector.onRemoveStart.async();

            DELETE( `/${ id }` )
                .then( E.planSelector.onRemoveResult )
                .catch( pTap( console.error ))
                .catch( E.planSelector.onRemoveError )
                .then( E.planSelector.onGetList );
        },

        onRenamePlan: ({ id, name }, { E, getStoreState }) => {

            const {
                plans,
                renameLoading,
            } = getStoreState( 'planSelector' );

            const plan =    plans.find( plan => plan.id === id );

            if( renameLoading || !id || !plan || !name ) {
                return;
            }

            E.planSelector.onRenameStart.async();

            PUT( `/${ id }`, {
                ...plan,
                name,
            })
                .then( E.planSelector.onRenameResult )
                .catch( pTap( console.error ))
                .catch( E.planSelector.onRenameError );
        },
    },
};
