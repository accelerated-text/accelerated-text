import pTap                 from 'p-tap';

import jsonToBlockly    from './json-to-blockly';
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
                .then( jsonToBlockly )
                .then( E.tokenizer.onCallResult )
                .catch( pTap( console.error ))
                .catch( E.tokenizer.onCallError );
        },
    },
};
