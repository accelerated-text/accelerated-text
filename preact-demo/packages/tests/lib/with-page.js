import puppeteer            from 'puppeteer';

import addPageAssertions    from './add-page-assertions';


/// Copied from NPM package jest-environment-puppeteer/lib/readConfig.js:
const CHROME_CI_ARGS = [
    '--disable-background-timer-throttling',
    '--disable-backgrounding-occluded-windows',
    '--disable-renderer-backgrounding',
    '--disable-setuid-sandbox',
    '--no-sandbox',
];


export default async ( t, run ) => {

    const browser =     await puppeteer.launch({
        args:           process.env.CI === 'true' ? CHROME_CI_ARGS : [],
        defaultViewport: {
            width:      1024,
            height:     768,
        },
    });
    const page =        await browser.newPage();

    try {
        await run( addPageAssertions( t ), page );
    } finally {
        await page.close();
        await browser.close();
    }
};
