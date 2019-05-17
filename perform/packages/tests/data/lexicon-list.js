/* eslint-disable no-plusplus */

let timestamp =     +new Date - 1e6;

export const createPhrase = synonyms => ({
    createdAt:      timestamp++,
    updatedAt:      timestamp++,
    key:            `example-${ synonyms[0] }.1`,
    synonyms:       synonyms.map( item => `example ${ item }` ),
});

export default {
    offset:         0,
    totalCount:     40,
    limit:          11,
    items: [
        createPhrase([ 1, 'one' ]),
        createPhrase([ 2, 'two' ]),
        createPhrase([ 3, 'three' ]),
        createPhrase([ 4, 'four' ]),
        createPhrase([ 5, 'five' ]),
        createPhrase([ 6, 'six' ]),
        createPhrase([ 7, 'seven' ]),
        createPhrase([ 8, 'eight' ]),
        createPhrase([ 9, 'nine' ]),
        createPhrase([ 10, 'ten' ]),
        createPhrase([ 11, 'eleven' ]),
    ],
};
