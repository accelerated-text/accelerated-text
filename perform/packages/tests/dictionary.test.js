import test                     from 'ava';

import {
    areDictionaryItemsVisible,
}   from './lib/dictionary-utils';
import defaultResponsesPage     from './lib/default-responses-page';
import DICTIONARY               from './data/dictionary';
import noRecordsPage            from './lib/no-records-page';
import { SELECTORS }            from './constants';


test( 'default elements visible', defaultResponsesPage, async t => {
    t.timeout( 5e3 );

    await t.findElement( SELECTORS.DICTIONARY_ITEM );
    await t.findElement( SELECTORS.DICTIONARY_ITEM_NAME );
    await t.findElement( SELECTORS.DICTIONARY_ITEM_PHRASES );

    await areDictionaryItemsVisible( t, DICTIONARY.dictionary );
});


test( 'correct elements when no files', noRecordsPage, async t => {
    t.timeout( 5e3 );

    await t.notFindElement( SELECTORS.DICTIONARY_ITEM );
    await t.notFindElement( SELECTORS.DICTIONARY_ITEM_NAME );
    await t.notFindElement( SELECTORS.DICTIONARY_ITEM_PHRASES );
});
