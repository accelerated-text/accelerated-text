import test             from 'ava';

import defaultResponses from './lib/default-responses';
import noRecords        from './lib/no-records';
import { SELECTORS }    from './constants';
import withPage         from './lib/with-page';


test( 'no list when no records', withPage, async ( t, page ) => {
    t.timeout( 5e3 );

    await noRecords( page );

    await t.notFindElement( page, SELECTORS.LEXICON_LIST );
    await t.notFindElement( page, SELECTORS.LEXICON_ITEM );
    await t.notFindElement( page, SELECTORS.LEXICON_NEW_ITEM );
});


test( 'default list', withPage, async ( t, page ) => {
    t.timeout( 5e3 );

    await defaultResponses( page );

    await t.findElement( page, SELECTORS.LEXICON_LIST );
    await t.findElement( page, SELECTORS.LEXICON_ITEM );
    await t.notFindElement( page, SELECTORS.LEXICON_NEW_ITEM );
});


test( 'add new item form', withPage, async ( t, page ) => {
    t.timeout( 5e3 );

    await noRecords( page );

    await page.click( SELECTORS.LEXICON_NEW_BTN );

    await t.findElement( page, SELECTORS.LEXICON_NEW_ITEM );
    await t.findElement( page, SELECTORS.LEXICON_EDIT );
    await t.findElement( page, SELECTORS.LEXICON_EDIT_TEXT );
    await t.findElement( page, SELECTORS.LEXICON_EDIT_SAVE );
    await t.findElement( page, SELECTORS.LEXICON_EDIT_CANCEL );
});

test.todo( 'search works' );
test.todo( 'search debouncing works' );
test.todo( 'loading more items works' );
test.todo( 'adding item works' );
test.todo( 'editing item works' );
