import * as nlgApi      from '../nlg-api';


const PREFIX =          '/data';


export const fetch = ( path, options = {}) =>
    nlgApi( `${ PREFIX }${ path }`, options );

export const GET = path =>
    nlgApi.GET( `${ PREFIX }${ path }` );
