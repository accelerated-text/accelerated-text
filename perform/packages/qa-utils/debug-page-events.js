/*  eslint-disable no-console */

const EVENT_NAMES = [
    'close',
    'console',
    'dialog',
    'domcontentloaded',
    'error',
    'frameattached',
    'framedetached',
    'framenavigated',
    'load',
    'metrics',
    'pageerror',
    'popup',
    'request',
    'requestfailed',
    'requestfinished',
    'response',
    'workercreated',
    'workerdestroyed',
];

module.exports = ( page, eventNames = EVENT_NAMES ) => {

    eventNames.forEach( name => {
        page.on( name, arg => {
            console.log( 'ON', name, arg );
        });
    });
};
