import AwesomeDebouncePromise from 'awesome-debounce-promise';

import * as nlgApi          from '../nlg-api';


const PREFIX =              '/lexicon';


export const fetch = ( path, options = {}) =>
    nlgApi( `${ PREFIX }${ path }`, options );

export const DELETE = path =>
    nlgApi.DELETE( `${ PREFIX }${ path }` );

export const GET = path =>
    nlgApi.GET( `${ PREFIX }${ path }` );

export const POST = ( path, params ) =>
    nlgApi.POST( `${ PREFIX }${ path }`, params );

export const PUT = ( path, params ) =>
    nlgApi.PUT( `${ PREFIX }${ path }`, params );

export const search = ({ query, offset }) => {
    
    const params =  new URLSearchParams();
    if( offset ) {
        params.append( 'offset', offset );
    }
    if( query ) {
        params.append( 'query', `*${ query }*` );
    }

    return GET( '?' + params.toString());
};

export const debouncedSearch = AwesomeDebouncePromise( search, 200 );
