import puppeteer        from 'puppeteer';


/// Copied from NPM package jest-environment-puppeteer/lib/readConfig.js:
const CHROME_CI_ARGS = [
    '--disable-background-timer-throttling',
    '--disable-backgrounding-occluded-windows',
    '--disable-renderer-backgrounding',
    '--disable-setuid-sandbox',
    '--no-sandbox',
];

const SELECTOR_WAIT_OPTIONS = {
    timeout:            500,
};


export default async ( t, run ) => {

    const browser =     await puppeteer.launch({
        args:           process.env.CI === 'true' ? CHROME_CI_ARGS : [],
        defaultViewport: {
            width:      1024,
            height:     768,
        },
    });
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
