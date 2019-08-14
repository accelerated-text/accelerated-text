const https =               require( 'https' );

const CORS_HEADERS = {
    'access-control-allow-headers':	'content-type, *',
    'access-control-allow-methods':	'*',
    'access-control-allow-origin':	'*',
};

const objectAdd = ( obj, k, v ) => {
    obj[k] = v;
    return obj;
};

const filterHeaders = obj =>
    Object.entries( obj )
        .reduce(
            ( acc, [ k, v ]) => (
                v instanceof Array
                    ? acc
                    : objectAdd( acc, k, v )
            ),
            {}
        );
        
const fixGraphqlErrors = body => {
    let fixed =             body;
  
    try {
        fixed =             JSON.parse( body );
        const shouldFixErrors = (
            fixed.errors
            && !( fixed.errors instanceof Array )
        );
        if( shouldFixErrors ) {
            fixed.errors =
                Object.entries( fixed.errors )
                    .map(([ k, v ]) => ({
                        message:    v,
                        locations:  k,
                        path:       k,
                    }));
        }
        fixed =             JSON.stringify( fixed );
    } catch( e ) {}
    return fixed;
};


const onError = callback => err => {

    let body =          err.toString();
    let isJson =        false;
    try {
        body =          JSON.stringify( err );
        isJson =        true;
    } catch{}
    callback( null, {
        isBase64Encoded:    false,
        statusCode:         501,
        headers: {
            'Content-Type': isJson ? 'application/json' : 'text/plain',
            ...CORS_HEADERS,
        },
        body,

    });
};

const onResponseEnd = res => {

    res.on( 'end', ( res, body, callback ) => {
        console.log( 'body', body );
        callback( null, {
            isBase64Encoded:    false,
            statusCode:         res.statusCode,
            body:               fixGraphqlErrors( body ),
            headers: {
                ...filterHeaders( res.headers ),
                ...CORS_HEADERS,
            },
        });
    });
};

exports.handler = ( event, context, callback ) => {
    console.log( 'API_URL', process.env.API_URL );
    console.log( 'ACCESS_TOKEN', typeof process.env.ACCESS_TOKEN );
    console.log( 'headers', JSON.stringify( event.headers ));
    console.log( 'body', event.body );
    const req = https.request(
        process.env.API_URL,
        {
            method:         'POST',
            headers: {
                'Content-type':             event.headers['content-type'] || 'application/json',
                'X-Shopify-Access-Token':   process.env.ACCESS_TOKEN,
            },
        },
        res => {
            let body =      '';
            console.log( 'Status:', res.statusCode );
            res.setEncoding( 'utf8' );
            res.on( 'data', chunk => body += chunk );
            res.on( 'end', () => onResponseEnd( res, body, callback ));
        }
    );
    req.on( 'error', onError( callback ));
    req.write( event.body );
    req.end();
};
