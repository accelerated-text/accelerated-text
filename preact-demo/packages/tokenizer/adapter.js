import pTap             from 'p-tap';

import tokenizer        from './tokenizer';


export default {

    tokenizer: {

        onCall: ( text, { E, getStoreState }) => {

            const { loading } =     getStoreState( 'tokenizer' );

            if( loading || !text ) {
                return;
            }

            E.tokenizer.onCallStart.async();

            tokenizer( text )
                .then( E.tokenizer.onCallResult )
                .catch( pTap( console.error ))
                .catch( E.tokenizer.onCallError );
        },
    },
};
