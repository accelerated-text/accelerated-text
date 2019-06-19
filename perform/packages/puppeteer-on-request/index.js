const { zipObj } =      require( 'ramda' );

const requestMatchers = require( './request-matchers' );


const ONCE =            'ONCE';
const ALWAYS =          'ALWAYS';

const CONTINUE =        'CONTINUE';
const INTERCEPT =       'INTERCEPT';
const PROVIDE =         'PROVIDE';


const matchUrl = ( method, url ) => request => {

    const reqMethod =   request.method();
    const reqUrl =      request.url();

    return (
        ( method === reqMethod
            || ( method.test && method.test( reqMethod )))
        && ( url === reqUrl
            || ( url.test && url.test( reqUrl )))
    );
};

const matchFn = ( methodOrFn, urlOrOther ) => (
    methodOrFn instanceof Function
        ? methodOrFn
        : matchUrl( methodOrFn, urlOrOther )
);

const matchArgs = ( argNames, args ) => (
    args[0] instanceof Function
        ? zipObj( argNames, args.slice( 1 ))
        : zipObj( argNames, args.slice( 2 ))
);


module.exports = async ( page, options = {}) => {

    const matchers =    requestMatchers();

    const addMatcher = ( occurance, type, ...argNames ) =>
        ( ...args ) =>
            ( occurance === ONCE )
                ? new Promise(( resolve, reject ) =>
                    matchers.add( matchFn( ...args ), {
                        ...matchArgs( argNames, args ),
                        occurance,
                        type,
                        resolve,
                        reject,
                    })
                )
                : matchers.add( matchFn( ...args ), {
                    ...matchArgs( argNames, args ),
                    occurance,
                    type,
                });

    const continueAll =     addMatcher( ALWAYS, CONTINUE );
    const continueOnce =    addMatcher( ONCE,   CONTINUE );
    const interceptAll =    addMatcher( ALWAYS, INTERCEPT,  'onRequestFn' );
    const interceptOnce =   addMatcher( ONCE,   INTERCEPT,  'onRequestFn' );
    const provideAll =      addMatcher( ALWAYS, PROVIDE,    'body', 'status', 'headers' );
    const provideOnce =     addMatcher( ONCE,   PROVIDE,    'body', 'status', 'headers' );

    const onRequest = async request => {

        const method =  request.method();
        const url =     request.url();

        if( options.onRequest ) {
            options.onRequest({ method, request, url });
        }

        const matcher = matchers.findMatch( request );
        if( !matcher ) {
            const err = Error( `Got unexpected request for ${ method } ${ url }.` );
            if( options.onError ) {
                options.onError( err );
                return;
            } else {
                throw err;
            }
        }

        const {
            status =        200,
            headers =       {},
        } = matcher;

        switch( matcher.type ) {

        case CONTINUE:
            request.continue();
            break;

        case INTERCEPT:
            matcher.onRequestFn( request, method, url );
            break;

        case PROVIDE:
            request.respond({
                status,
                headers,
                contentType:    headers.contentType || 'application/json',
                body: (
                    headers.contentType
                        ? matcher.body
                        : JSON.stringify( matcher.body )
                ),
            });
            break;

        default:
            const err = Error( `Unrecognized matcher type ${ matcher.type } for ${ method } ${ url }.` );   // eslint-disable-line no-case-declarations

            if( options.onError ) {
                options.onError( err );
            } else {
                throw err;
            }
        }

        if( matcher.occurance === ONCE ) {
            matcher.resolve({ request });
            matchers.remove( matcher );
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
        clear:              matchers.reset,
        continueAll,
        continueOnce,
        interceptAll,
        interceptOnce,
        provideAll,
        provideOnce,
        startInterception,
        stopInterception,
    };
};
