import test                     from 'ava';

import {
    arePhrasesVisible,
    isItemNameVisible,
    openItem,
}                               from './lib/dictionary-editor-utils';
import defaultResponsesPage     from './lib/default-responses-page';
import { SELECTORS }            from './constants';


test( 'editor opens and closes', defaultResponsesPage, async t => {

    await t.notFindElement( SELECTORS.DICT_ITEM_EDITOR );

    const item =            await openItem( t, 0 );

    await t.findElement( SELECTORS.DICT_ITEM_EDITOR );
    await t.findElement( SELECTORS.DICT_ITEM_EDITOR_DELETE );
    await isItemNameVisible( t, item );

    await t.page.click( SELECTORS.DICT_ITEM_EDITOR_CLOSE );
    await t.notFindElement( SELECTORS.DICT_ITEM_EDITOR );
});


test( 'correct phrases visible', defaultResponsesPage, async t => {

    const item0 =           await openItem( t, 0 );

    await arePhrasesVisible( t, item0.phrases );
    await t.notFindElement( `${ SELECTORS.DICT_ITEM_EDITOR_PHRASE_DEFAULT_USAGE } ${ SELECTORS.USAGE_TD_DONT_CARE }` );

    const item1 =           await openItem( t, 1 );

    await arePhrasesVisible( t, item1.phrases );
    await t.notFindElement( `${ SELECTORS.DICT_ITEM_EDITOR_PHRASE_DEFAULT_USAGE } ${ SELECTORS.USAGE_TD_DONT_CARE }` );
});
