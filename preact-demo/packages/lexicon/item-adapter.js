import { POST, PUT }        from './api';

export default {

    lexiconItem: {

        onCancelEdit: ( _, { props }) =>
            props.onCancel.async(),

        onSave: ( _, { E, getStoreState, props }) => {

            const {
                item: { key, synonyms },
            } = getStoreState( 'lexiconItem' );

            if( !key ) {
                POST( '/', { synonyms })
                    .then( item => {
                        E.lexiconItem.onSaveSuccess( item );
                        props.onSave && props.onSave( item );
                    })
                    .catch( E.lexiconItem.onSaveError );
            } else {
                PUT( `/${ key }`, { synonyms })
                    .then( E.lexiconItem.onSaveSuccess )
                    .catch( E.lexiconItem.onSaveError );
            }
        },
    },
};
