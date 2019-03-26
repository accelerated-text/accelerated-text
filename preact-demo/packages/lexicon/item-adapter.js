import { PUT }          from './api';

export default {

    lexiconItem: {

        onUpdate: ( _, { E, getStoreState }) => {

            const {
                item: { key, synonyms },
            } = getStoreState( 'lexiconItem' );

            if( !key ) {
                E.lexiconItem.onUpdateError( 'Can\'t update item until it is saved on server.' );
            } else {
                PUT( `/${ key }`, { synonyms })
                    .then( E.lexiconItem.onUpdateSuccess )
                    .catch( E.lexiconItem.onUpdateError );
            }
        },
    },
};
