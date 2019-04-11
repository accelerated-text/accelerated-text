const requestHandlers = require( './request-handlers' );

const ONCE =            'ONCE';
const ALWAYS =          'ALWAYS';

const CONTINUE =        'CONTINUE';
const INTERCEPT =       'INTERCEPT';
const PROVIDE =         'PROVIDE';


module.exports = async ( page, options = {}) => {

    const handlers =    requestHandlers();

    const addHandler = ( occurance, type, method, url, fields ) =>
        ( occurance === ONCE )
            ? new Promise(( resolve, reject ) =>
                handlers.add( method, url, {
                    ...fields,
                    occurance,
                    type,
                    resolve,
                    reject,
                })
            )
            : handlers.add( method, url, {
                ...fields,
                occurance,
                type,
            });

    const continueAll = ( method, url ) =>
        addHandler( ALWAYS, CONTINUE, method, url );

    const continueOnce = ( method, url ) =>
        addHandler( ONCE, CONTINUE, method, url );

    const interceptAll = ( method, url, onRequestFn ) =>
        addHandler( ALWAYS, INTERCEPT, method, url, { onRequestFn });

    const interceptOnce = ( method, url, onRequestFn ) =>
        addHandler( ONCE, INTERCEPT, method, url, { onRequestFn });

    const provideAll = ( method, url, body, status = 200, headers = {}) =>
        addHandler( ALWAYS, PROVIDE, method, url, { body, status, headers });

    const provideOnce = ( method, url, body, status = 200, headers = {}) =>
        addHandler( ONCE, PROVIDE, method, url, { body, status, headers });

    const onRequest = async request => {

        const method =  request.method();
        const url =     request.url();

        if( options.onRequest ) {
            options.onRequest({ method, request, url });
        }

        const handler = handlers.findMatch( method, url );
        if( !handler ) {
            const err = Error( `Got unexpected request for ${ method } ${ url }.` );
            if( options.onError ) {
                options.onError( err );
            } else {
                throw err;
            }
        }
        if( options.onRequestHandler ) {
            options.onRequestHandler({ handler, method, request, url });
        }

        switch( handler.type ) {

        case CONTINUE:
            request.continue();
            break;

        case INTERCEPT:
            handler.onRequestFn( request, method, url );
            break;

        case PROVIDE:
            request.respond({
                status:         handler.status,
                headers:        handler.headers,
                contentType:    handler.headers.contentType || 'application/json',
                body: (
                    handler.headers.contentType
                        ? handler.body
                        : JSON.stringify( handler.body )
                ),
            });
            break;

        default:
            const err = Error( `Unrecognized handler type ${ handler.type } for ${ method } ${ url }.` );   // eslint-disable-line no-case-declarations
            if( options.onError ) {
                options.onError( err );
            } else {
                throw err;
            }
        }

        if( handler.occurance === ONCE ) {
            handler.resolve({ request });
            handlers.remove( handler );
        }
    };

    const startInterception = async page => {
        await page.setRequestInterception( true );
        return page.on( 'request', onRequest );
    };

    const stopInterception = page => {
        page.removeListener( 'request', onRequest );
        return page.setRequestInterception( false );
    };

    /// Main
    await startInterception( page );

    return {
        clear:          handlers.reset,
        continue:       continueOnce,
        continueAll,
        continueOnce,
        intercept:      interceptOnce,
        interceptAll,
        interceptOnce,
        provide:        provideOnce,
        provideAll,
        provideOnce,
        startInterception,
        stopInterception,
    };
};
