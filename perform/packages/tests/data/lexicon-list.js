/* eslint-disable no-plusplus */

let timestamp =     +new Date - 1e6;

const makeExample = synonyms => ({
    createdAt:      timestamp++,
    updatedAt:      timestamp++,
    key:            `example-${ synonyms[0] }.1`,
    synonyms:       synonyms.map( item => `example ${ item }` ),
});

module.exports = {
    offset:         0,
    totalCount:     40,
    limit:          11,
    items: [
        makeExample([ 1, 'one' ]),
        makeExample([ 2, 'two' ]),
        makeExample([ 3, 'three' ]),
        makeExample([ 4, 'four' ]),
        makeExample([ 5, 'five' ]),
        makeExample([ 6, 'six' ]),
        makeExample([ 7, 'seven' ]),
        makeExample([ 8, 'eight' ]),
        makeExample([ 9, 'nine' ]),
        makeExample([ 10, 'ten' ]),
        makeExample([ 11, 'eleven' ]),
    ],
};
