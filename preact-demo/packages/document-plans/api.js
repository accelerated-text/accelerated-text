import fetch            from 'isomorphic-unfetch';


const BASE_URL =        'https://zihxo6d93h.execute-api.eu-central-1.amazonaws.com/Prod/document-plans';


const apiFetch = async ( path, options = {}) => {

    const response =    await fetch( `${ BASE_URL }${ path }`, {
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


export default apiFetch;

export const DELETE = path =>
    apiFetch( path, {
        method: 'DELETE',
    });

export const GET = path =>
    apiFetch( path );

export const POST = ( path, params ) =>
    apiFetch( path, {
        method:         'POST',
        body:           JSON.stringify( params ),
    });

export const PUT = ( path, params ) =>
    apiFetch( path, {
        method:         'PUT',
        body:           JSON.stringify( params ),
    });
