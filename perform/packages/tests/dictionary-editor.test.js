import test                     from 'ava';

import {
    arePhrasesVisible,
    openItem,
}   from './lib/dictionary-editor-utils';
import defaultResponsesPage     from './lib/default-responses-page';
import { PhraseUsage }          from './data/dictionary';
import { SELECTORS }            from './constants';


test( 'editor opens and closes', defaultResponsesPage, async t => {
    t.timeout( 5e3 );

    await t.notFindElement( SELECTORS.DICT_ITEM_EDITOR );

    const item =            await openItem( t, 1 );

    await t.findElement( SELECTORS.DICT_ITEM_EDITOR );
    await t.is(
        await t.getElementText( SELECTORS.DICT_ITEM_EDITOR_NAME ),
        item.name,
    );

    await t.page.click( SELECTORS.DICT_ITEM_EDITOR_CLOSE );
    await t.notFindElement( SELECTORS.DICT_ITEM_EDITOR );
});


test( 'phrases visible', defaultResponsesPage, async t => {
    t.timeout( 5e3 );

    const item0 =           await openItem( t, 0 );

    await arePhrasesVisible( t, item0.usageModels );

    const item1 =           await openItem( t, 1 );

    await arePhrasesVisible( t, item1.usageModels );
});


test( 'add phrase works', defaultResponsesPage, async t => {

    const defaultUsage =    'YES';
    const phrase =          'zzzzzzz';

    const item =            await openItem( t, 0 );

    await t.findElement( SELECTORS.DICT_ITEM_EDITOR_ADD_PHRASE );

    await t.page.type( SELECTORS.DICT_ITEM_EDITOR_ADD_PHRASE_TEXT, phrase );

    const updatedItem = {
        ...item,
        usageModels: [
            ...item.usageModels,
            PhraseUsage({
                prefix:         item.id,
                phrase,
                defaultUsage,
                readerUsage: {
                    junior:     'DONT_CARE',
                    senior:     'DONT_CARE',
                },
            }),
        ],
    };

    const response = t.graphqlApi.provideOnce(
        'createPhraseUsageModel',
        {
            dictionaryItemId:   item.id,
            phrase,
            defaultUsage,
        },
        { data: { createPhraseUsageModel: updatedItem }},
    );

    await t.page.click( SELECTORS.DICT_ITEM_EDITOR_ADD_PHRASE_ADD );
    await response;
    await arePhrasesVisible( t, updatedItem.usageModels );
});
