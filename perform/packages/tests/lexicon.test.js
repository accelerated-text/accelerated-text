import test                 from 'ava';

import defaultResponsesPage from './lib/default-responses-page';
import noRecordsPage        from './lib/no-records-page';
import { SELECTORS }        from './constants';


test( 'no list when no records', noRecordsPage, async t => {
    t.timeout( 5e3 );

    await t.notFindElement( SELECTORS.LEXICON_LIST );
    await t.notFindElement( SELECTORS.LEXICON_ITEM );
    await t.notFindElement( SELECTORS.LEXICON_NEW_ITEM );
});


test( 'default list', defaultResponsesPage, async t => {
    t.timeout( 5e3 );

    await t.findElement( SELECTORS.LEXICON_LIST );
    await t.findElement( SELECTORS.LEXICON_ITEM );
    await t.notFindElement( SELECTORS.LEXICON_NEW_ITEM );
});


test( 'add new item form', noRecordsPage, async t => {
    t.timeout( 5e3 );

    await t.page.click( SELECTORS.LEXICON_NEW_BTN );

    await t.findElement( SELECTORS.LEXICON_NEW_ITEM );
    await t.findElement( SELECTORS.LEXICON_EDIT );
    await t.findElement( SELECTORS.LEXICON_EDIT_TEXT );
    await t.findElement( SELECTORS.LEXICON_EDIT_SAVE );
    await t.findElement( SELECTORS.LEXICON_EDIT_CANCEL );
});

test.todo( 'search works' );
test.todo( 'search debouncing works' );
test.todo( 'loading more items works' );
test.todo( 'adding item works' );
test.todo( 'editing item works' );
