/* eslint-disable no-plusplus */

let timestamp =     +new Date - 1e6;

export const createPhrases = phrases => ({
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
        createPhrases([ 1, 'one' ]),
        createPhrases([ 2, 'two' ]),
        createPhrases([ 3, 'three' ]),
        createPhrases([ 4, 'four' ]),
        createPhrases([ 5, 'five' ]),
        createPhrases([ 6, 'six' ]),
        createPhrases([ 7, 'seven' ]),
        createPhrases([ 8, 'eight' ]),
        createPhrases([ 9, 'nine' ]),
        createPhrases([ 10, 'ten' ]),
        createPhrases([ 11, 'eleven' ]),
    ],
};
