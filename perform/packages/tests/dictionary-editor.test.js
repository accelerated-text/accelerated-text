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

    const item =            await openItem( t, 0 );

    await t.findElement( SELECTORS.DICT_ITEM_EDITOR );
    t.is(
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
    await t.notFindElement( `${ SELECTORS.DICT_ITEM_EDITOR_PHRASE_DEFAULT_USAGE } ${ SELECTORS.USAGE_TD_DONT_CARE }` );

    const item1 =           await openItem( t, 1 );

    await arePhrasesVisible( t, item1.usageModels );
    await t.notFindElement( `${ SELECTORS.DICT_ITEM_EDITOR_PHRASE_DEFAULT_USAGE } ${ SELECTORS.USAGE_TD_DONT_CARE }` );
});


test( 'add phrase works', defaultResponsesPage, async t => {
    t.timeout( 5e3 );

    const defaultUsage =    'YES';
    const phrase =          'zzzzzzz';

    const item =            await openItem( t, 2 );
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

    await t.findElement( SELECTORS.DICT_ITEM_EDITOR_ADD_PHRASE );

    await t.page.type( SELECTORS.DICT_ITEM_EDITOR_ADD_PHRASE_TEXT, phrase );

    t.page.click( SELECTORS.DICT_ITEM_EDITOR_ADD_PHRASE_ADD );
    await t.graphqlApi.provideOnce(
        'createPhraseUsageModel',
        {
            dictionaryItemId:   item.id,
            phrase,
            defaultUsage,
        },
        { data: { createPhraseUsageModel: updatedItem }},
    );

    await arePhrasesVisible( t, updatedItem.usageModels );
});


test( 'changing defaultUsage works', defaultResponsesPage, async t => {
    t.timeout( 5e3 );

    const item =            await openItem( t, 0 );
    const phrase =          item.usageModels[0];
    const updatedPhrase = {
        ...phrase,
        defaultUsage:       'NO',
    };

    t.page.click( `${ SELECTORS.DICT_ITEM_EDITOR_PHRASE } > td:nth-child( 2 ) > ${ SELECTORS.USAGE_TD_NO }` );

    await t.graphqlApi.provideOnce(
        'updatePhraseUsageModelDefault',
        {
            id:             phrase.id,
            defaultUsage:   updatedPhrase.defaultUsage,
        },
        { data: { updatePhraseUsageModelDefault: updatedPhrase }}
    );
});
