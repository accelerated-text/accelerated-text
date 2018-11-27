import fetch                from 'isomorphic-unfetch';

import { host, mountPath }  from './constants';
    

const analysisFetch =       async ( path, options = null ) =>
    fetch( mountPath + path, {
        ...options,
        headers: {
            Accept:         'application/json',
            ...( options && options.headers || null ),
        },
    }).then(
        response => response.json()
    );

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


