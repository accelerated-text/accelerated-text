import test                     from 'ava';

import {
    areDictionaryItemsVisible,
}                               from './lib/dictionary-utils';
import {
    arePhrasesVisible,
    openItem,
}                               from './lib/dictionary-editor-utils';
import defaultResponsesPage     from './lib/default-responses-page';
import {
    default as DICTIONARY,
    Phrase,
}                               from './data/dictionary';
import { READER_FLAGS }         from './data/reader-flags';
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

test( 'can rename item', defaultResponsesPage, async t => {
    t.timeout( 5e3 );

    const item =            await openItem( t, 0 );
    const newName =         t.title;
    const updatedItem = {
        ...item,
        name:               newName,
    };

    await t.page.click( SELECTORS.DICT_ITEM_EDITOR_NAME );

    await t.findElement( SELECTORS.DICT_ITEM_EDITOR_NAME_INPUT );
    await t.findElement( SELECTORS.DICT_ITEM_EDITOR_NAME_SAVE );
    await t.findElement( SELECTORS.DICT_ITEM_EDITOR_NAME_CANCEL );

    await t.retypeElementText( SELECTORS.DICT_ITEM_EDITOR_NAME_INPUT, newName );
    t.page.click( SELECTORS.DICT_ITEM_EDITOR_NAME_SAVE );
    await t.graphqlApi.provideOnce(
        'updateDictionaryItem',
        {
            id:             item.id,
            name:           newName,
        },
        { data: { updateDictionaryItem: updatedItem }},
    );

    await t.findElement( SELECTORS.DICT_ITEM_EDITOR_NAME );
    t.is(
        await t.getElementText( SELECTORS.DICT_ITEM_EDITOR_NAME ),
        updatedItem.name,
    );
});

/*
test( 'can cancel rename item', defaultResponsesPage, async t => {
    t.timeout( 5e3 );

    const item =            await openItem( t, 1 );
    const newName =         t.title;

    await t.page.click( SELECTORS.DICT_ITEM_EDITOR_NAME );

    await t.findElement( SELECTORS.DICT_ITEM_EDITOR_NAME_INPUT );
    await t.findElement( SELECTORS.DICT_ITEM_EDITOR_NAME_SAVE );
    await t.findElement( SELECTORS.DICT_ITEM_EDITOR_NAME_CANCEL );

    await t.retypeElementText( SELECTORS.DICT_ITEM_EDITOR_NAME_INPUT, newName );
    t.page.click( SELECTORS.DICT_ITEM_EDITOR_NAME_CANCEL );

    await t.findElement( SELECTORS.DICT_ITEM_EDITOR_NAME );
    t.is(
        await t.getElementText( SELECTORS.DICT_ITEM_EDITOR_NAME ),
        item.name,
    );
});

test( 'can delete item', defaultResponsesPage, async t => {
    t.timeout( 5e3 );

    const num =             0;
    const item =            await openItem( t, num );
    const updatedDictionary = {
        ...DICTIONARY,
        dictionary: {
            ...DICTIONARY.dictionary,
            items:          DICTIONARY.dictionary.items.splice( num, 1 ),
        },
    };

    t.page.click( SELECTORS.DICT_ITEM_EDITOR_DELETE );
    await t.graphqlApi.provideOnce(
        'deleteDictionaryItem',
        { id:   item.id },
        { data: { deleteDictionaryItem: true }},
    );
    await t.graphqlApi.provideOnce(
        'dictionary',
        {},
        { data: updatedDictionary },
    );

    await t.notFindElement( SELECTORS.DICT_ITEM_EDITOR_NAME );
    await areDictionaryItemsVisible( t, updatedDictionary.dictionary.items );
});
*/

test( 'phrases visible', defaultResponsesPage, async t => {
    t.timeout( 5e3 );

    const item0 =           await openItem( t, 0 );

    await arePhrasesVisible( t, item0.phrases );
    await t.notFindElement( `${ SELECTORS.DICT_ITEM_EDITOR_PHRASE_DEFAULT_USAGE } ${ SELECTORS.USAGE_TD_DONT_CARE }` );

    const item1 =           await openItem( t, 1 );

    await arePhrasesVisible( t, item1.phrases );
    await t.notFindElement( `${ SELECTORS.DICT_ITEM_EDITOR_PHRASE_DEFAULT_USAGE } ${ SELECTORS.USAGE_TD_DONT_CARE }` );
});

test.todo( 'can rename phrase' );
test.todo( 'can delete phrase' );


test( 'add phrase works', defaultResponsesPage, async t => {
    t.timeout( 5e3 );

    const defaultUsage =    'YES';
    const text =            'zzzzzzz';

    const item =            await openItem( t, 2 );
    const updatedItem = {
        ...item,
        phrases: [
            ...item.phrases,
            Phrase({
                prefix:         item.id,
                text,
                defaultUsage,
                readerFlagUsage: {
                    [READER_FLAGS[0]]:  'DONT_CARE',
                    [READER_FLAGS[1]]:  'DONT_CARE',
                },
            }),
        ],
    };

    await t.findElement( SELECTORS.DICT_ITEM_EDITOR_ADD_PHRASE );

    await t.page.type( SELECTORS.DICT_ITEM_EDITOR_ADD_PHRASE_TEXT, text );

    t.page.click( SELECTORS.DICT_ITEM_EDITOR_ADD_PHRASE_ADD );
    await t.graphqlApi.provideOnce(
        'createPhrase',
        {
            dictionaryItemId:   item.id,
            text,
            defaultUsage,
        },
        { data: { createPhrase: updatedItem }},
    );

    await arePhrasesVisible( t, updatedItem.phrases );
});


test( 'changing defaultUsage works', defaultResponsesPage, async t => {
    t.timeout( 5e3 );

    const item =            await openItem( t, 0 );
    const phrase =          item.phrases[0];
    const updatedPhrase = {
        ...phrase,
        defaultUsage:       'NO',
    };

    t.page.click( `${ SELECTORS.DICT_ITEM_EDITOR_PHRASE } > td:nth-child( 2 ) > ${ SELECTORS.USAGE_TD_NO }` );

    await t.graphqlApi.provideOnce(
        'updatePhraseDefaultUsage',
        {
            id:             phrase.id,
            defaultUsage:   updatedPhrase.defaultUsage,
        },
        { data: { updatePhraseDefaultUsage: updatedPhrase }}
    );

    await arePhrasesVisible( t, [
        updatedPhrase,
        ...item.phrases.slice( 1 ),
    ]);
});


test( 'changing readerFlagUsage works', defaultResponsesPage, async t => {
    t.timeout( 5e3 );

    const item =            await openItem( t, 1 );
    const phrase =          item.phrases[0];
    const readerFlagUsage = phrase.readerFlagUsage[0];
    const usage =           readerFlagUsage.usage === 'NO' ? 'YES' : 'NO';
    const updatedReaderFlagUsage = {
        ...readerFlagUsage,
        usage,
    };
    const updatedPhrase = {
        ...phrase,
        readerFlagUsage: [
            updatedReaderFlagUsage,
            ...phrase.readerFlagUsage.slice( 1 ),
        ],
    };
    const updatedItem = {
        ...item,
        phrases: [
            updatedPhrase,
            ...item.phrases.slice( 1 ),
        ],
    };

    t.page.click( `${ SELECTORS.DICT_ITEM_EDITOR_PHRASE }:nth-child( 1 ) > td:nth-child( 3 ) > ${ SELECTORS.USAGE_TD_NO }` );

    await t.graphqlApi.provideOnce(
        'updateReaderFlagUsage',
        {
            id:             readerFlagUsage.id,
            usage,
        },
        { data: { updateReaderFlagUsage: updatedReaderFlagUsage }},
    );

    await arePhrasesVisible( t, updatedItem.phrases );
});
