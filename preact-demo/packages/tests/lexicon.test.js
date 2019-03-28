const defaultResponses =    require( './response-templates/default' );
const noRecords =           require( './response-templates/no-records' );

const { SELECTORS } =       require( './constants' );


describe( 'no records', () => {

    test( 'no list when no records', async () => {

        const page =        await browser.newPage();
        await noRecords( page );

        await expect( page ).not.toMatchElement( SELECTORS.LEXICON_LIST );
        await expect( page ).not.toMatchElement( SELECTORS.LEXICON_ITEM );
        await expect( page ).not.toMatchElement( SELECTORS.LEXICON_NEW_ITEM );

        await page.close();
    }, 10e3 );

    test( 'default list', async () => {

        const page =        await browser.newPage();
        await defaultResponses( page );
        
        await expect( page ).toMatchElement( SELECTORS.LEXICON_LIST );
        await expect( page ).toMatchElement( SELECTORS.LEXICON_ITEM );
        await expect( page ).not.toMatchElement( SELECTORS.LEXICON_NEW_ITEM );

        await page.close();
    }, 20e3 );

    test( 'add new item form', async () => {

        const page =        await browser.newPage();
        await noRecords( page );

        await page.click( SELECTORS.LEXICON_NEW_BTN );

        await expect( page ).toMatchElement( SELECTORS.LEXICON_NEW_ITEM );
        await expect( page ).toMatchElement( SELECTORS.LEXICON_EDIT );
        await expect( page ).toMatchElement( SELECTORS.LEXICON_EDIT_TEXT );
        await expect( page ).toMatchElement( SELECTORS.LEXICON_EDIT_SAVE );
        await expect( page ).toMatchElement( SELECTORS.LEXICON_EDIT_CANCEL );

        await page.close();
    }, 10e3 );
});
