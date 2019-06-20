import test                     from 'ava';

import defaultResponsesPage     from './lib/default-responses-page';
import DICTIONARY               from './data/dictionary';
import { SELECTORS }            from './constants';


test( 'editor opens and closes', defaultResponsesPage, async t => {
    t.timeout( 5e3 );

    const item =                DICTIONARY.dictionary[1];
    const itemSelector =        `${ SELECTORS.DICTIONARY_ITEM }:nth-child(2)`;

    await t.notFindElement( SELECTORS.DICT_ITEM_EDITOR );
    await t.page.click( `${ itemSelector } ${ SELECTORS.DICTIONARY_ITEM_NAME }` );
    
    await t.findElement( SELECTORS.DICT_ITEM_EDITOR );
    await t.is(
        await t.getElementText( SELECTORS.DICT_ITEM_EDITOR_NAME ),
        item.name,
    );

    await t.page.click( SELECTORS.DICT_ITEM_EDITOR_CLOSE );
    await t.notFindElement( SELECTORS.DICT_ITEM_EDITOR );
});
