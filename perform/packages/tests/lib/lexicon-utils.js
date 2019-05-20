import { SELECTORS }        from '../constants';


export const getLexiconSearchUrl = ({ query = '', offset = 0 }) => {

    const params =  new URLSearchParams();
    if( offset ) {
        params.append( 'offset', offset );
    }
    if( query ) {
        params.append( 'query', `*${ query }*` );
    }

    return '/lexicon?' + params.toString();
};


export const areLexiconItemsVisible = async ( t, items ) => {

    await t.resetMouse();

    for( let i = 0; i < items.length; i += 1 ) {

        const item =            items[i];
        const itemSelector =    `${ SELECTORS.LEXICON_ITEM }:nth-child(${ i + 1 })`;

        t.is(
            await t.getElementProperty(
                `${ itemSelector } ${ SELECTORS.LEXICON_ITEM_ID }`,
                'innerText',
            ),
            item.key,
        );

        t.is(
            await t.getElementProperty(
                `${ itemSelector } ${ SELECTORS.LEXICON_ITEM_PHRASES }`,
                'innerText',
            ),
            item.synonyms.join( '' ),
        );
    }
};
