import test                     from 'ava';

import { DELETE_CONFIRM }       from '../ui-messages/on-confirm-delete';
import { DONT_CARE, NO, YES }   from '../usage/constants';

import {
    arePhrasesVisible,
    openItem,
}                               from './lib/dictionary-editor-utils';
import defaultResponsesPage     from './lib/default-responses-page';
import { Phrase }               from './data/dictionary';
import { READER_FLAGS }         from './data/reader-flags';
import { SELECTORS }            from './constants';
import withPageDialogs          from './lib/with-page-dialogs';


test( 'can rename phrase', defaultResponsesPage, async t => {

    const num =             0;
    const item =            await openItem( t, num );
    const phrase =          item.phrases[ num ];
    const $phraseText =     `${ SELECTORS.DICT_ITEM_EDITOR_PHRASE }:nth-child( ${ num + 1 } ) ${ SELECTORS.DICT_ITEM_EDITOR_PHRASE_TEXT }`;
    const newText =         t.title;
    const updatedPhrase = {
        ...phrase,
        text:               newText,
    };

    await arePhrasesVisible( t, item.phrases );

    await t.page.click( $phraseText );

    await t.findElement( SELECTORS.DICT_ITEM_EDITOR_PHRASE_TEXT_CANCEL );
    await t.findElement( SELECTORS.DICT_ITEM_EDITOR_PHRASE_TEXT_INPUT );
    await t.findElement( SELECTORS.DICT_ITEM_EDITOR_PHRASE_TEXT_SAVE );

    await t.retypeElementText( SELECTORS.DICT_ITEM_EDITOR_PHRASE_TEXT_INPUT, newText );
    t.page.click( SELECTORS.DICT_ITEM_EDITOR_PHRASE_TEXT_SAVE );
    await t.graphqlApi.provideOnce(
        'updatePhrase',
        {
            id:             phrase.id,
            text:           newText,
        },
        { data: { updatePhrase: updatedPhrase }},
    );
    t.is(
        await t.getElementText( $phraseText ),
        updatedPhrase.text,
    );
});


test( 'can delete phrase', defaultResponsesPage, withPageDialogs, async t => {

    const itemNum =         0;
    const phraseNum =       1;
    const item =            await openItem( t, itemNum );
    const phrase =          item.phrases[ phraseNum ];
    const updatedItem = {
        ...item,
        phrases:            [ ...item.phrases ].splice( phraseNum, 1 ),
    };

    await arePhrasesVisible( t, item.phrases );

    t.page.click(
        `${ SELECTORS.DICT_ITEM_EDITOR_PHRASE }:nth-child(${ phraseNum + 1 }) ${ SELECTORS.DICT_ITEM_EDITOR_PHRASE_DELETE }`
    );
    await t.acceptDialog( 'confirm', DELETE_CONFIRM );
    await t.graphqlApi.provideOnce(
        'deletePhrase',
        { id:               phrase.id },
        { data: { deletePhrase: updatedItem }},
    );

    await arePhrasesVisible( t, updatedItem.phrases );
});


test( 'can cancel phrase delete', defaultResponsesPage, withPageDialogs, async t => {

    const item =            await openItem( t, 0 );

    await arePhrasesVisible( t, item.phrases );

    t.page.click( SELECTORS.DICT_ITEM_EDITOR_PHRASE_DELETE );
    await t.dismissDialog( 'confirm', DELETE_CONFIRM );

    await arePhrasesVisible( t, item.phrases );
});


test( 'add phrase works', defaultResponsesPage, async t => {

    const defaultUsage =    YES;
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
                    [READER_FLAGS[0]]:  DONT_CARE,
                    [READER_FLAGS[1]]:  DONT_CARE,
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

    const item =            await openItem( t, 0 );
    const phrase =          item.phrases[0];
    const updatedPhrase = {
        ...phrase,
        defaultUsage:       NO,
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

    const item =            await openItem( t, 1 );
    const phrase =          item.phrases[0];
    const readerFlagUsage = phrase.readerFlagUsage[0];
    const usage =           readerFlagUsage.usage === NO ? YES : NO;
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
