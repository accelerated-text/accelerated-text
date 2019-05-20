import test                 from 'ava';

import {
    areLexiconItemsVisible,
    getLexiconSearchUrl,
}   from './lib/lexicon-utils';
import { createPhrases }    from './data/lexicon-list';
import defaultResponsesPage from './lib/default-responses-page';
import noRecordsPage        from './lib/no-records-page';
import { SELECTORS }        from './constants';


test( 'no records', noRecordsPage, async t => {
    t.timeout( 5e3 );

    await t.findElement( SELECTORS.LEXICON_NEW_BTN );
    await t.findElement( SELECTORS.LEXICON_SEARCH );

    await t.findElement( SELECTORS.LEXICON_LIST );
    await t.findElement( SELECTORS.LEXICON_NO_ITEMS );

    await t.notFindElement( SELECTORS.LEXICON_ITEM );
    await t.notFindElement( SELECTORS.LEXICON_MORE );
    await t.notFindElement( SELECTORS.LEXICON_NEW_ITEM );
});


test( 'default list', defaultResponsesPage, async t => {
    t.timeout( 5e3 );

    await t.findElement( SELECTORS.LEXICON_NEW_BTN );
    await t.findElement( SELECTORS.LEXICON_SEARCH );

    await t.findElement( SELECTORS.LEXICON_LIST );
    await t.notFindElement( SELECTORS.LEXICON_NO_ITEMS );

    await t.findElement( SELECTORS.LEXICON_ITEM );
    await t.findElement( SELECTORS.LEXICON_MORE );
    await t.notFindElement( SELECTORS.LEXICON_NEW_ITEM );
});


test( 'search works', noRecordsPage, async t => {
    t.timeout( 5e3 );

    const query =       'test';

    t.page.type( SELECTORS.LEXICON_SEARCH, query, { delay: 10 });

    let results = {
        limit:          4,
        offset:         0,
        totalCount:     5,
        items: [
            createPhrases([ 1 ]),
            createPhrases([ 2, 20 ]),
            createPhrases([ 3, 30, 31 ]),
            createPhrases([ 4, 40, 41, 42 ]),
        ],
    };
    await t.nlgApi.provideOnce( 'GET', getLexiconSearchUrl({ query }), results );
    await t.waitUntilElementGone( SELECTORS.UI_LOADING );

    await t.findElement( SELECTORS.LEXICON_MORE );
    await areLexiconItemsVisible( t, results.items );

    t.clearInput( SELECTORS.LEXICON_SEARCH );

    results = {
        limit:          2,
        offset:         0,
        totalCount:     2,
        items: [
            createPhrases([ 1, 2, 3, 4 ]),   /// same key (1) as above, but different synonyms
            createPhrases([ 5 ]),            /// new key (5) and synonyms
        ],
    };
    await t.nlgApi.provideOnce( 'GET', getLexiconSearchUrl({}), results );
    await t.waitUntilElementGone( SELECTORS.UI_LOADING );

    await t.notFindElement( SELECTORS.LEXICON_MORE );
    await areLexiconItemsVisible( t, results.items );
});


test( 'loading more items works', noRecordsPage, async t => {
    t.timeout( 5e3 );

    const query =       'test';

    t.page.type( SELECTORS.LEXICON_SEARCH, query, { delay: 10 });

    const results0 = {
        limit:          1,
        offset:         0,
        totalCount:     3,
        items:          [ createPhrases([ 0, 1, 2 ]) ],
    };

    await t.nlgApi.provideOnce( 'GET', getLexiconSearchUrl({ query }), results0 );
    await t.waitUntilElementGone( SELECTORS.UI_LOADING );

    await areLexiconItemsVisible( t, results0.items );
    await t.findElement( SELECTORS.LEXICON_MORE );

    t.page.click( SELECTORS.LEXICON_MORE );

    const results1 = {
        ...results0,
        offset:         1,
        items:          [ createPhrases([ 1, 2, 3 ]) ],
    };
    await t.nlgApi.provideOnce(
        'GET',
        getLexiconSearchUrl({ query, offset: 1 }),
        results1,
    );

    await areLexiconItemsVisible( t, [ ...results0.items, ...results1.items ]);
    await t.findElement( SELECTORS.LEXICON_MORE );

    t.page.click( SELECTORS.LEXICON_MORE );

    const results2 = {
        ...results0,
        offset:         2,
        items:          [ createPhrases([ 2, 3, 4 ]) ],
    };
    await t.nlgApi.provideOnce(
        'GET',
        getLexiconSearchUrl({ query, offset: 2 }),
        results2,
    );

    await areLexiconItemsVisible( t, [
        ...results0.items,
        ...results1.items,
        ...results2.items,
    ]);
    await t.notFindElement( SELECTORS.LEXICON_MORE );
});

test.todo( 'search debouncing works' );


test.todo( 'adding item works' );


test( 'add new item form', noRecordsPage, async t => {
    t.timeout( 5e3 );

    await t.page.click( SELECTORS.LEXICON_NEW_BTN );

    await t.findElement( SELECTORS.LEXICON_NEW_ITEM );
    await t.findElement( SELECTORS.LEXICON_EDIT );
    await t.findElement( SELECTORS.LEXICON_EDIT_TEXT );
    await t.findElement( SELECTORS.LEXICON_EDIT_SAVE );
    await t.findElement( SELECTORS.LEXICON_EDIT_CANCEL );
});


test.todo( 'editing item works' );
