/* eslint-disable no-plusplus */
import { range }            from 'ramda';

import {
    T_LEXICON_LIST,
    T_LEXICON_ITEM,
}   from './graphql-types';


let timestamp =     +new Date - 1e6;


export const createLexiconItem = phrases => ({
    __typename:     T_LEXICON_LIST,
    createdAt:      timestamp++,
    key:            `example-${ phrases[0] }.1`,
    synonyms:       phrases.map( item => `example ${ item }` ),
    updatedAt:      timestamp++,
});


export const createLexiconList = ({
    items =         null,
    limit =         20,
    offset =        0,
    totalCount =    100,
}) => ({
    __typename:     T_LEXICON_ITEM,
    limit:          items && items.length || limit,
    offset,
    totalCount,
    items: (
        items
        || range( offset, limit )
            .map( num =>
                createLexiconItem([ num, `a${ num }`, `b${ num }` ])
            )
    ),
});


export default createLexiconList({});
