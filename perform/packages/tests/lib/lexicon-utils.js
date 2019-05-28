import { SELECTORS }        from '../constants';


export const areLexiconItemsVisible = async ( t, items ) => {

    await t.resetMouse();

    for( let i = 0; i < items.length; i += 1 ) {

        const item =            items[i];
        const itemSelector =    `${ SELECTORS.LEXICON_ITEM }:nth-child(${ i + 1 })`;

        t.is(
            await t.getElementText( `${ itemSelector } ${ SELECTORS.LEXICON_ITEM_ID }` ),
            item.key,
        );

        t.is(
            await t.getElementText( `${ itemSelector } ${ SELECTORS.LEXICON_ITEM_PHRASES }` ),
            item.synonyms.join( '' ),
        );
    }
};


export const createItemSelectors = itemSelector => ({
    EDIT:           `${ itemSelector } ${ SELECTORS.LEXICON_EDIT }`,
    EDIT_CANCEL:    `${ itemSelector } ${ SELECTORS.LEXICON_EDIT_CANCEL }`,
    EDIT_SAVE:      `${ itemSelector } ${ SELECTORS.LEXICON_EDIT_SAVE }`,
    EDIT_TEXT:      `${ itemSelector } ${ SELECTORS.LEXICON_EDIT_TEXT }`,
    EDIT_ERROR:     `${ itemSelector } ${ SELECTORS.UI_ERROR }`,
    EDIT_LOADING:   `${ itemSelector } ${ SELECTORS.UI_LOADING }`,
    ITEM_BLOCK:     `${ itemSelector } ${ SELECTORS.LEXICON_ITEM_BLOCK }`,
    ITEM_ID:        `${ itemSelector } ${ SELECTORS.LEXICON_ITEM_ID }`,
    ITEM_PHRASES:   `${ itemSelector } ${ SELECTORS.LEXICON_ITEM_PHRASES }`,
});
