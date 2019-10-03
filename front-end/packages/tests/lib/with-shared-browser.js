import launchBrowser        from './launch-browser';


let browser;

export default async ( t, run, ...args ) => {
    browser =               browser || launchBrowser();

    return run(
        Object.assign( t, {
            browser:        await browser,
        }),
        ...args,
    );
};
