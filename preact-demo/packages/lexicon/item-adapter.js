import { PUT }          from './api';

export default {

    lexiconItem: {

        onSave: ( _, { E, getStoreState }) => {

            const {
                item: { key, synonyms },
            } = getStoreState( 'lexiconItem' );

            if( !key ) {
                E.lexiconItem.onSaveError( 'Can\'t update item until it is saved on server.' );
            } else {
                PUT( `/${ key }`, { synonyms })
                    .then( E.lexiconItem.onSaveSuccess )
                    .catch( E.lexiconItem.onSaveError );
            }
        },
    },
};
