import * as nlgApi      from '../nlg-api';


const PREFIX =          '/document-plans';


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
