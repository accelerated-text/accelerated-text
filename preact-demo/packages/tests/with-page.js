import puppeteer        from 'puppeteer';


const SELECTOR_WAIT_OPTIONS = {
    timeout:            500,
};


export default async ( t, run ) => {

    const browser =     await puppeteer.launch();
    const page =        await browser.newPage();

    t.findElement = ( page, selector ) =>
        t.notThrowsAsync( page.waitForSelector( selector, SELECTOR_WAIT_OPTIONS ));

    t.notFindElement = ( page, selector ) =>
        t.throwsAsync( page.waitForSelector( selector, SELECTOR_WAIT_OPTIONS ));

    try {
        await run( t, page );
    } finally {
        await page.close();
        await browser.close();
    }
};
