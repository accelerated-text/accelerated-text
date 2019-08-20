import puppeteer            from 'puppeteer';


/// Copied from NPM package jest-environment-puppeteer/lib/readConfig.js:
const CHROME_CI_ARGS = [
    '--disable-background-timer-throttling',
    '--disable-backgrounding-occluded-windows',
    '--disable-dev-shm-usage',
    '--disable-gpu',
    '--disable-renderer-backgrounding',
    '--disable-setuid-sandbox',
    '--no-first-run',
    '--no-sandbox',
    '--no-zygote',
    '--single-process',
];


export default () =>
    puppeteer.launch({
        args:           process.env.CI === 'true' ? CHROME_CI_ARGS : [],
        defaultViewport: {
            width:      1024,
            height:     768,
        },
        timeout:        0,
    });
