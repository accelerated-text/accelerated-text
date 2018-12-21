import pTap             from 'p-tap';

import tokenizer        from './tokenizer';


export default {

    planEditor: {

        onSubmitTextExample: ({ text }, { E, getStoreState }) => {

            /// Prevent new requests while the previous one is not finished:
            if( getStoreState( 'tokenizer' ).loading ) {
                return;
            }

            E.tokenizer.onCall.async();

            tokenizer( text )
                .then( E.tokenizer.onCallResult )
                .catch( pTap( console.error ))
                .catch( E.tokenizer.onCallError );
        },
    },
};
