import { SELECTORS }        from '../constants';


export const areDictionaryItemsVisible = async ( t, items ) => {

    for( let i = 0; i < items.length; i += 1 ) {

        const item =            items[i];
        const itemSelector =    `${ SELECTORS.DICTIONARY_ITEM }:nth-child(${ i + 1 })`;

        t.is(
            await t.getElementText( `${ itemSelector } ${ SELECTORS.DICTIONARY_ITEM_NAME }` ),
            item.name,
        );

        t.is(
            await t.getElementText( `${ itemSelector } ${ SELECTORS.DICTIONARY_ITEM_PHRASES }` ),
            item.usageModels.map( phrase => phrase.phrase ).join( '' ),
        );
    }
};


export const getItemSelector = n =>
    `${ SELECTORS.DICTIONARY_ITEM }:nth-child( ${ n + 1 } )`;
