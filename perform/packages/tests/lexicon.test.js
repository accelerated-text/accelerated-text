import test                 from 'ava';

import {
    areLexiconResultsVisible,
    getLexiconSearchUrl,
}   from './lib/lexicon-utils';
import { createPhrase }     from './data/lexicon-list';
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
            createPhrase([ 1 ]),
            createPhrase([ 2, 20 ]),
            createPhrase([ 3, 30, 31 ]),
            createPhrase([ 4, 40, 41, 42 ]),
        ],
    };
    await t.nlgApi.provideOnce( 'GET', getLexiconSearchUrl({ query }), results );
    await t.waitUntilElementGone( SELECTORS.UI_LOADING );

    await t.findElement( SELECTORS.LEXICON_MORE );
    await areLexiconResultsVisible( t, results );

    t.clearInput( SELECTORS.LEXICON_SEARCH );

    results = {
        limit:          2,
        offset:         0,
        totalCount:     2,
        items: [
            createPhrase([ 1, 2, 3, 4 ]),   /// same key (1) as above, but different synonyms
            createPhrase([ 5 ]),            /// new key (5) and synonyms
        ],
    };
    await t.nlgApi.provideOnce( 'GET', getLexiconSearchUrl({}), results );
    await t.waitUntilElementGone( SELECTORS.UI_LOADING );

    await t.notFindElement( SELECTORS.LEXICON_MORE );
    await areLexiconResultsVisible( t, results );
});


test.todo( 'loading more items works' );

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
