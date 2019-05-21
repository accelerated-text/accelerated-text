import test                 from 'ava';

import {
    areLexiconItemsVisible,
    createItemSelectors,
    getLexiconSearchUrl,
}   from './lib/lexicon-utils';
import {
    createLexiconItem,
    default as LEXICON_LIST,
}   from './data/lexicon-list';
import defaultResponsesPage from './lib/default-responses-page';
import noRecordsPage        from './lib/no-records-page';
import { SELECTORS }        from './constants';


const SEL_NEW_ITEM =        createItemSelectors( SELECTORS.LEXICON_NEW_ITEM );


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

    await areLexiconItemsVisible( t, LEXICON_LIST.items );
});


test( 'search works', noRecordsPage, async t => {
    t.timeout( 5e3 );

    const query =       'test';

    t.page.type( SELECTORS.LEXICON_SEARCH, query );

    let results = {
        limit:          4,
        offset:         0,
        totalCount:     5,
        items: [
            createLexiconItem([ 1 ]),
            createLexiconItem([ 2, 20 ]),
            createLexiconItem([ 3, 30, 31 ]),
            createLexiconItem([ 4, 40, 41, 42 ]),
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
            createLexiconItem([ 1, 2, 3, 4 ]),   /// same key (1) as above, but different synonyms
            createLexiconItem([ 5 ]),            /// new key (5) and synonyms
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

    t.page.type( SELECTORS.LEXICON_SEARCH, query );

    const results0 = {
        limit:          1,
        offset:         0,
        totalCount:     3,
        items:          [ createLexiconItem([ 0, 1, 2 ]) ],
    };

    await t.nlgApi.provideOnce( 'GET', getLexiconSearchUrl({ query }), results0 );
    await t.waitUntilElementGone( SELECTORS.UI_LOADING );

    await areLexiconItemsVisible( t, results0.items );
    await t.findElement( SELECTORS.LEXICON_MORE );

    t.page.click( SELECTORS.LEXICON_MORE );

    const results1 = {
        ...results0,
        offset:         1,
        items:          [ createLexiconItem([ 1, 2, 3 ]) ],
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
        items:          [ createLexiconItem([ 2, 3, 4 ]) ],
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


test( 'add new item form', noRecordsPage, async t => {
    t.timeout( 5e3 );

    await t.page.click( SELECTORS.LEXICON_NEW_BTN );

    await t.findElement( SELECTORS.LEXICON_NEW_ITEM );
    await t.findElement( SEL_NEW_ITEM.EDIT );
    await t.findElement( SEL_NEW_ITEM.EDIT_TEXT );
    await t.findElement( SEL_NEW_ITEM.EDIT_SAVE );
    await t.findElement( SEL_NEW_ITEM.EDIT_CANCEL );
});

test( 'add item cancel works', noRecordsPage, async t => {
    t.timeout( 5e3 );

    await t.page.click( SELECTORS.LEXICON_NEW_BTN );
    await t.page.click( SEL_NEW_ITEM.EDIT_CANCEL );

    await t.notFindElement( SELECTORS.LEXICON_NEW_ITEM );
    await t.notFindElement( SEL_NEW_ITEM.EDIT );
    await t.notFindElement( SEL_NEW_ITEM.EDIT_TEXT );
    await t.notFindElement( SEL_NEW_ITEM.EDIT_SAVE );
    await t.notFindElement( SEL_NEW_ITEM.EDIT_CANCEL );
});


test( 'add item save works', noRecordsPage, async t => {
    t.timeout( 5e3 );

    const item =        createLexiconItem([ 'one', 'two', 'three' ]);

    await t.page.click( SELECTORS.LEXICON_NEW_BTN );

    t.is(
        await t.getElementText( SEL_NEW_ITEM.ITEM_ID ),
        ''
    );

    await t.page.type( SEL_NEW_ITEM.EDIT_TEXT, item.synonyms.join( '\n' ));
    t.page.click( SEL_NEW_ITEM.EDIT_SAVE );

    await t.nlgApi.interceptOnce( 'POST', '/lexicon/', request => {
        t.is(
            request.postData(),
            JSON.stringify({ synonyms: item.synonyms }),
        );
        return t.nlgApi.respond( request, item );
    });
    await t.waitUntilElementGone( SELECTORS.UI_LOADING );

    t.is(
        await t.getElementText( SEL_NEW_ITEM.ITEM_ID ),
        item.key,
    );
    t.is(
        await t.getElementText( SEL_NEW_ITEM.ITEM_PHRASES ),
        item.synonyms.join( '' ),
    );
});


test.todo( 'add item errors handled' );


test( 'edit item form', defaultResponsesPage, async t => {
    t.timeout( 5e3 );

    const item =        LEXICON_LIST.items[0];
    const SEL_ITEM =    createItemSelectors( `${ SELECTORS.LEXICON_ITEM }:first-child` );

    t.page.click( SEL_ITEM.ITEM_PHRASES );

    await t.findElement( SEL_ITEM.EDIT );
    await t.findElement( SEL_ITEM.EDIT_TEXT );
    await t.findElement( SEL_ITEM.EDIT_SAVE );
    await t.findElement( SEL_ITEM.EDIT_CANCEL );

    t.is(
        await t.getElementValue( SEL_ITEM.EDIT_TEXT ),
        item.synonyms.join( '\n' ),
    );
});


test( 'edit item cancel works', defaultResponsesPage, async t => {
    t.timeout( 5e3 );

    const SEL_ITEM =    createItemSelectors( `${ SELECTORS.LEXICON_ITEM }:nth-child(2)` );

    await t.page.click( SEL_ITEM.ITEM_PHRASES );
    await t.page.click( SEL_ITEM.EDIT_CANCEL );

    await t.notFindElement( SEL_ITEM.EDIT );
    await t.notFindElement( SEL_ITEM.EDIT_TEXT );
    await t.notFindElement( SEL_ITEM.EDIT_SAVE );
    await t.notFindElement( SEL_ITEM.EDIT_CANCEL );
});


test( 'edit item save works', defaultResponsesPage, async t => {
    t.timeout( 5e3 );

    const item =        LEXICON_LIST.items[2];
    const newItem = {
        ...item,
        synonyms:       [ t.title, 1, 2, 3, 4, 5 ],
    };
    const itemUrl =     `/lexicon/${ encodeURIComponent( item.key )}`;
    const SEL_ITEM =    createItemSelectors( `${ SELECTORS.LEXICON_ITEM }:nth-child(3)` );

    await t.page.click( SEL_ITEM.ITEM_PHRASES );
    await t.clearInput( SEL_ITEM.EDIT_TEXT );
    await t.page.type( SEL_ITEM.EDIT_TEXT, newItem.synonyms.join( '\n' ));

    t.page.click( SEL_ITEM.EDIT_SAVE );

    await t.nlgApi.provideOnce( 'OPTIONS', itemUrl, '' );
    await t.nlgApi.provideOnce( 'PUT', itemUrl, newItem );
    await t.waitUntilElementGone( SEL_ITEM.EDIT_LOADING );

    t.is(
        await t.getElementText( SEL_ITEM.ITEM_ID ),
        item.key,
    );
    t.is(
        await t.getElementText( SEL_ITEM.ITEM_PHRASES ),
        newItem.synonyms.join( '' ),
    );
});

test.todo( 'edit item errors handled' );

test.todo( 'disabled editing while loading' );
