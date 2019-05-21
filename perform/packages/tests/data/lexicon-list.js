/* eslint-disable no-plusplus */

let timestamp =     +new Date - 1e6;

export const createLexiconItem = phrases => ({
    createdAt:      timestamp++,
    updatedAt:      timestamp++,
    key:            `example-${ phrases[0] }.1`,
    synonyms:       phrases.map( item => `example ${ item }` ),
});

export default {
    offset:         0,
    totalCount:     40,
    limit:          11,
    items: [
        createLexiconItem([ 1, 'one' ]),
        createLexiconItem([ 2, 'two' ]),
        createLexiconItem([ 3, 'three' ]),
        createLexiconItem([ 4, 'four' ]),
        createLexiconItem([ 5, 'five' ]),
        createLexiconItem([ 6, 'six' ]),
        createLexiconItem([ 7, 'seven' ]),
        createLexiconItem([ 8, 'eight' ]),
        createLexiconItem([ 9, 'nine' ]),
        createLexiconItem([ 10, 'ten' ]),
        createLexiconItem([ 11, 'eleven' ]),
    ],
};
