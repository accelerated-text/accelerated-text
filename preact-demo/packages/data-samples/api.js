import * as nlgApi      from '../nlg-api';


const PREFIX =          '/data';

const fixListResult = list =>
    list.filter( item => item.fieldNames && item.fieldNames.length > 1 )
        .map(( item, i ) => ({
            ...item,
            contentType:    item.contentType || 'text/csv',
            id:             item.key,
            fileName:       item.key.split( '/' ).pop(),
        }));


export const fetch = ( path, options = {}) =>
    nlgApi( `${ PREFIX }${ path }`, options );

export const GET = path =>
    nlgApi.GET( `${ PREFIX }${ path }` );

export const getList = userId =>
    GET( `/?user=${ encodeURIComponent( userId )}` )
        .then( fixListResult );
