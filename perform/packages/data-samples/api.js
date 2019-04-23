import * as nlgApi          from '../nlg-api';

import { addItemFields }    from './functions';


const PREFIX =              '/data';


export const fetch = ( path, options = {}) =>
    nlgApi( `${ PREFIX }${ path }`, options );

export const GET = path =>
    nlgApi.GET( `${ PREFIX }${ path }` );

export const getData = fileItem =>
    GET( `/${ fileItem.key }` );

export const getList = userId =>
    GET( `/?user=${ encodeURIComponent( userId )}` )
        .then( list => list.map( addItemFields ));
