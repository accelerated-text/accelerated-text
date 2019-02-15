import debugSan         from '../debug-san/';

import tokenizer        from './tokenizer';


const debug =           debugSan( 'tokenizer/adapter' );


export default {

    tokenizer: {

        onCall: ( text, { E, getStoreState }) => {

            const { loading } =     getStoreState( 'tokenizer' );

            debug( 'onCall', text, { loading });
            if( loading || !text ) {
                return;
            }

            E.tokenizer.onCallStart.async();

            tokenizer( text )
                .then( debug.tapThen( 'onCall result' ))
                .then( E.tokenizer.onCallResult )
                .catch( debug.tapCatch( 'onCall error' ))
                .catch( E.tokenizer.onCallError );
        },
    },
};
