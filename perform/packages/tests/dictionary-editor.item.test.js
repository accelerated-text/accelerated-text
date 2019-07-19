import test                     from 'ava';

import { DELETE_CONFIRM }       from '../ui-messages/on-confirm-delete';

import {
    areDictionaryItemsVisible,
}                               from './lib/dictionary-utils';
import defaultResponsesPage     from './lib/default-responses-page';
import DICTIONARY               from './data/dictionary';
import { openItem }             from './lib/dictionary-editor-utils';
import { SELECTORS }            from './constants';
import withPageDialogs          from './lib/with-page-dialogs';


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

    t.is(
        await t.getElementText( SELECTORS.DICT_ITEM_EDITOR_NAME ),
        updatedItem.name,
    );
});


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


test( 'can delete item', defaultResponsesPage, withPageDialogs, async t => {
    t.timeout( 5e3 );

    const num =             0;
    const item =            await openItem( t, num );
    const updatedDictionary = {
        ...DICTIONARY,
        dictionary: {
            ...DICTIONARY.dictionary,
            items:          [ ...DICTIONARY.dictionary.items ].splice( num, 1 ),
        },
    };

    await t.findElement( SELECTORS.DICT_ITEM_EDITOR_DELETE );
    t.page.click( SELECTORS.DICT_ITEM_EDITOR_DELETE );
    await t.acceptDialog( 'confirm', DELETE_CONFIRM );
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

    await areDictionaryItemsVisible( t, updatedDictionary.dictionary.items );
    await t.notFindElement( SELECTORS.DICT_ITEM_EDITOR_NAME );
});


test( 'can cancel item delete', defaultResponsesPage, withPageDialogs, async t => {
    t.timeout( 5e3 );

    const item =            await openItem( t, 0 );

    t.is(
        await t.getElementText( SELECTORS.DICT_ITEM_EDITOR_NAME ),
        item.name,
    );
    await t.findElement( SELECTORS.DICT_ITEM_EDITOR_DELETE );
    t.page.click( SELECTORS.DICT_ITEM_EDITOR_DELETE );
    await t.dismissDialog( 'confirm', DELETE_CONFIRM );

    t.is(
        await t.getElementText( SELECTORS.DICT_ITEM_EDITOR_NAME ),
        item.name,
    );
});
