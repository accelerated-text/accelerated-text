import DICTIONARY           from '../data/dictionary';
import { SELECTORS }        from '../constants';

import { getItemSelector }  from './dictionary-utils';


export const isPhraseVisible = async ( t, selPhrase, phrase ) => {

    t.is(
        await t.getElementText( `${ selPhrase } ${ SELECTORS.DICT_ITEM_EDITOR_PHRASE_TEXT }` ),
        phrase.phrase,
    );

    const selDefaultUsage = `${ selPhrase } ${ SELECTORS.DICT_ITEM_EDITOR_PHRASE_DEFAULT_USAGE }`;
    const selReaderUsage =  `${ selPhrase } ${ SELECTORS.DICT_ITEM_EDITOR_PHRASE_READER_USAGE }`;

    await t.findElement( `${ selDefaultUsage }${ SELECTORS[phrase.defaultUsage] }` );

    await Promise.all( phrase.readerUsage.map(
        ({ usage }, i ) =>
            t.findElement( `${ selReaderUsage }:nth-child(${ i + 1 + 2 })${ SELECTORS[usage] }` )
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

    await t.page.click( `${ getItemSelector( num )} ${ SELECTORS.DICTIONARY_ITEM_NAME }` );
    return dictionary[num];
};
