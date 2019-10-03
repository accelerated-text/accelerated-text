import posToString          from '../../part-of-speech/to-string';

import DICTIONARY           from '../data/dictionary';
import { SELECTORS }        from '../constants';

import {
    emptyThesaurusQuery,
}                           from './thesaurus-utils';
import { getItemSelector }  from './dictionary-utils';


export const isItemNameVisible = async ( t, item ) =>
    t.is(
        await t.getElementText( SELECTORS.DICT_ITEM_EDITOR_NAME ),
        item.name + posToString( item.partOfSpeech ),
    );

export const isPhraseVisible = async ( t, $phrase, phrase ) => {

    t.findElement( $phrase );
    t.is(
        await t.getElementText( `${ $phrase } ${ SELECTORS.DICT_ITEM_EDITOR_PHRASE_TEXT }` ),
        phrase.text,
    );

    const $defaultUsage =       `${ $phrase } ${ SELECTORS.DICT_ITEM_EDITOR_PHRASE_DEFAULT_USAGE }`;
    const $readerFlagUsage =    `${ $phrase } ${ SELECTORS.DICT_ITEM_EDITOR_PHRASE_RFLAG_USAGE }`;

    await t.findElement( `${ $defaultUsage }${ SELECTORS[phrase.defaultUsage] }` );

    await Promise.all( phrase.readerFlagUsage.map(
        ({ usage }, i ) =>
            t.findElement( `${ $readerFlagUsage }:nth-child(${ i + 1 + 2 })${ SELECTORS[usage] }` )
    ));
};


export const arePhrasesVisible = ( t, phrases ) =>
    Promise.all( phrases.map(( phrase, i ) =>
        isPhraseVisible(
            t,
            `${ SELECTORS.DICT_ITEM_EDITOR_PHRASE }:nth-child(${ i + 1 })`,
            phrase,
        )
    ));


export const openItem = async ( t, num, dictionary = DICTIONARY.dictionary ) => {

    await Promise.all([
        emptyThesaurusQuery( t ),
        t.page.click( `${ getItemSelector( num )} ${ SELECTORS.DICTIONARY_ITEM_NAME }` ),
    ]);
    return dictionary.items[num];
};
