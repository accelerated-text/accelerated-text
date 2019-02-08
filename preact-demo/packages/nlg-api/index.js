import unfetch            from 'isomorphic-unfetch';


const BASE_URL =        'https://zihxo6d93h.execute-api.eu-central-1.amazonaws.com/Prod';


export const fetch = async ( path, options = {}) => {

    const response =    await unfetch( `${ BASE_URL }${ path }`, {
        mode:           'cors',
        credentials:    'omit',
        cache:          'no-cache',
        ...options,
    });

    if( response.status >= 400 ) {
        throw Error( response.statusText );
    } else {
        const contentType = response.headers.get( 'Content-Type' );

        if( contentType === 'application/json' ) {
            return await response.json();
        } else {
            return await response.blob();
        }
    }
};


export const DELETE = path =>
    fetch( path, {
        method: 'DELETE',
    });

export const GET = path =>
    fetch( path );

export const POST = ( path, params ) =>
    fetch( path, {
        method:         'POST',
        body:           JSON.stringify( params ),
    });

export const PUT = ( path, params ) =>
    fetch( path, {
        method:         'PUT',
        body:           JSON.stringify( params ),
    });
