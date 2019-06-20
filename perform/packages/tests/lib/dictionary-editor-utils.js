import DICTIONARY           from '../data/dictionary';
import { SELECTORS }        from '../constants';

import { getItemSelector }  from './dictionary-utils';


export const arePhrasesVisible = async ( t, phrases ) => {

    for( let i = 0; i < phrases.length; i += 1 ) {

        const phrase =            phrases[i];
        const phraseSelector =    `${ SELECTORS.DICT_ITEM_EDITOR_PHRASE }:nth-child(${ i + 1 })`;

        t.is(
            await t.getElementText( `${ phraseSelector } ${ SELECTORS.DICT_ITEM_EDITOR_PHRASE_TEXT }` ),
            phrase.phrase,
        );
    }
};


export const openItem = async ( t, num, dictionary = DICTIONARY.dictionary ) => {

    await t.page.click( `${ getItemSelector( num )} ${ SELECTORS.DICTIONARY_ITEM_NAME }` );
    return dictionary[num];
};
