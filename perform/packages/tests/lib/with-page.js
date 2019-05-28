import puppeteer            from 'puppeteer';

import disableApolloAd      from './disable-apollo-devtools-ad';


/// Copied from NPM package jest-environment-puppeteer/lib/readConfig.js:
const CHROME_CI_ARGS = [
    '--disable-background-timer-throttling',
    '--disable-backgrounding-occluded-windows',
    '--disable-renderer-backgrounding',
    '--disable-setuid-sandbox',
    '--no-sandbox',
];


export default async ( t, run, ...args ) => {

    const browser =     await puppeteer.launch({
        args:           process.env.CI === 'true' ? CHROME_CI_ARGS : [],
        defaultViewport: {
            width:      1024,
            height:     768,
        },
    });
    const page =        await browser.newPage();

    disableApolloAd( page );

    try {
        await run( Object.assign( t, { browser, page }), ...args );
    } finally {
        await page.close();
        await browser.close();
    }
};
