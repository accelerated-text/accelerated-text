import test                     from 'ava';

import defaultResponsesPage     from './lib/default-responses-page';
import noRecordsPage            from './lib/no-records-page';
import { SELECTORS }            from './constants';


test( 'default elements visible', defaultResponsesPage, async t => {
    t.timeout( 5e3 );

    await t.findElement( SELECTORS.DICTIONARY_ITEM_NAME );
    await t.findElement( SELECTORS.DICTIONARY_ITEM_PHRASES );
});


test( 'correct elements when no files', noRecordsPage, async t => {
    t.timeout( 5e3 );

    await t.notFindElement( SELECTORS.DICTIONARY_ITEM_NAME );
    await t.notFindElement( SELECTORS.DICTIONARY_ITEM_PHRASES );
});
