import test                     from 'ava';

import {
    areDictionaryItemsVisible,
}   from './lib/dictionary-utils';
import defaultResponsesPage     from './lib/default-responses-page';
import DICTIONARY               from './data/dictionary';
import noRecordsPage            from './lib/no-records-page';
import { SELECTORS }            from './constants';


test( 'default elements visible', defaultResponsesPage, async t => {

    await t.findElements( SELECTORS, {
        DICTIONARY_ITEM:            true,
        DICTIONARY_ITEM_NAME:       true,
        DICTIONARY_ITEM_PHRASES:    true,
    });
    await areDictionaryItemsVisible( t, DICTIONARY.dictionary.items );
});


test( 'correct elements when no files', noRecordsPage, t =>
    t.findElements( SELECTORS, {
        DICTIONARY_ITEM:            false,
        DICTIONARY_ITEM_NAME:       false,
        DICTIONARY_ITEM_PHRASES:    false,
    }),
);
