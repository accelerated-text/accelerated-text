/* eslint-disable no-console */

/// Imports --------------------------------------------------------------------

const https =                       require( 'https' );


/// Constants ------------------------------------------------------------------

const API_URL =                     process.env.API_URL;
const ACCESS_TOKEN =                process.env.ACCESS_TOKEN;
const REQUEST_HEADERS = {
    'X-Shopify-Access-Token':       ACCESS_TOKEN,
};
const RESPONSE_HEADERS = {
    'access-control-allow-headers':	'content-type, *',
    'access-control-allow-methods':	'*',
    'access-control-allow-origin':	'*',
};


/// Functions ------------------------------------------------------------------

const objFilterValues = filterFn => obj => {
    const result =          {};
    const entries =         Object.entries( obj );
    for( const [ k, v ] of entries ) {
        if( filterFn( v )) {
            result[k] =     v;
        }
    }
    return result;
};

const keepOnlyStringValues = obj =>
    objFilterValues( v => typeof v === 'string' );

const fixGraphqlErrors = body => {
    let fixed =             body;

    try {
        fixed =             JSON.parse( body );
        if( fixed.errors
            && !( fixed.errors instanceof Array )
        ) {
            if( typeof fixed.errors === 'string' ) {
                fixed.errors = [{
                    message:        fixed.errors,
                    locations:      0,
                    path:           null,
                }];
            } else {
                fixed.errors =
                    Object.entries( fixed.errors )
                        .map(([ k, v ]) => ({
                            message:    v,
                            locations:  k,
                            path:       k,
                        }));
            }
        }
        fixed =             JSON.stringify( fixed );
    } catch( e ) {}
    return fixed;
};


/// Event handlers -------------------------------------------------------------

const onRequestError = callback => err => {
    console.log( 'onRequestError', err );

    let body =              err.toString();
    let isJson =            false;
    try {
        body =              JSON.stringify( err );
        isJson =            true;
    } catch( e ) {}

    callback( null, {
        isBase64Encoded:    false,
        statusCode:         501,
        headers: {
            'Content-Type': isJson ? 'application/json' : 'text/plain',
            ...RESPONSE_HEADERS,
        },
        body,

    });
};

const onResponseEnd = ( res, body, callback ) => {
    console.log( 'onResponseEnd', typeof res, body && body.length, typeof callback );

    callback( null, {
        isBase64Encoded:    false,
        statusCode:         res.statusCode,
        body:               fixGraphqlErrors( body ),
        headers: {
            ...keepOnlyStringValues( res.headers ),
            ...RESPONSE_HEADERS,
        },
    });
};


/// Main -----------------------------------------------------------------------

exports.handler = ( event, _, callback ) => {
    console.log( 'API_URL', API_URL );
    console.log( 'ACCESS_TOKEN', ACCESS_TOKEN && ACCESS_TOKEN.length );
    console.log( 'headers', JSON.stringify( event.headers ));
    console.log( 'body', event.body );

    const req = https.request(
        API_URL,
        {
            method:             'POST',
            headers: {
                ...REQUEST_HEADERS,
                'Content-type': event.headers['content-type'] || 'application/json',
            },
        },
        res => {
            let body =          '';
            console.log( 'Status:', res.statusCode );
            res.setEncoding( 'utf8' );
            res.on( 'data', chunk => {
                body += chunk;
            });
            res.on( 'end', () =>
                onResponseEnd( res, body, callback )
            );
        }
    );
    req.on( 'error', onRequestError( callback ));
    req.write( event.body );
    req.end();
};
