import fetch                from 'isomorphic-unfetch';

import { mountPath }        from './constants';
    

const analysisFetch =       async ( path, options = null ) => {

    const response = await fetch( mountPath + path, {
        ...options,
        headers: {
            Accept:         'application/json',
            ...( options && options.headers || null ),
        },
    });

    if( response.status >= 400 ) {
        throw Error( response.statusText );
    } else {
        return await response.json();
    }
};

export default analysisFetch;

export const POST =         async ( path, body = null, options = null ) =>
    analysisFetch( path, {
        method:             'POST',
        body:               JSON.stringify( body ),
        headers: {
            'Content-Type': 'application/json; charset=utf-8',
            ...( options && options.headers || null ),
        },
        ...options,
    });


