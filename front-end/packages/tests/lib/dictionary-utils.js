import posToString          from '../../part-of-speech/to-string';

import { SELECTORS }        from '../constants';


export const areDictionaryItemsVisible = async ( t, items ) => {

    for( let i = 0; i < items.length; i += 1 ) {

        const item =            items[i];
        const itemSelector =    `${ SELECTORS.DICTIONARY_ITEM }:nth-child(${ i + 1 })`;

        t.is(
            await t.getElementText( `${ itemSelector } ${ SELECTORS.DICTIONARY_ITEM_NAME }` ),
            item.name + posToString( item.partOfSpeech ),
        );

        t.is(
            await t.getElementText( `${ itemSelector } ${ SELECTORS.DICTIONARY_ITEM_PHRASES }` ),
            item.phrases.map( phrase => phrase.text ).join( '' ),
        );
    }
};


export const getItemSelector = n =>
    `${ SELECTORS.DICTIONARY_ITEM }:nth-child( ${ n + 1 } )`;
