import pTap             from 'p-tap';

import tokenizer        from './tokenizer';


export default {

    planEditor: {

        onSubmitTextExample: ( _, { E, getStoreState }) => {

            const { loading } =     getStoreState( 'tokenizer' );
            const { textExample } = getStoreState( 'planEditor' );

            /// Prevent new requests while the previous one is not finished:
            if( loading || !textExample ) {
                return;
            }

            E.tokenizer.onCall.async();

            tokenizer( textExample )
                .then( E.tokenizer.onCallResult )
                .catch( pTap( console.error ))
                .catch( E.tokenizer.onCallError );
        },
    },
};
