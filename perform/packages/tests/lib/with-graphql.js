import { applyTo, equals }  from 'ramda';


export const GRAPHQL_CORS_HEADERS = {
    'access-control-allow-headers': 'content-type, *',
    'access-control-allow-methods': 'POST, OPTIONS',
    'access-control-allow-origin':  '*',
};


export default ({ MACRO_NAME, URL }) => {

    const matchRequest = ( operationName, variables ) => request => (
        request.method() === 'POST'
        && request.url() === URL
        && applyTo(
            JSON.parse( request.postData()),
            postData => (
                postData.operationName === operationName
                && equals( postData.variables, variables )
            ),
        )
    );

    const graphqlApiRespond = ( request, body ) =>
        request.respond({
            status:         200,
            headers:        GRAPHQL_CORS_HEADERS,
            contentType:    'application/json',
            body: (
                typeof( body ) === 'string'
                    ? body
                    : JSON.stringify( body )
            ),
        });

    const graphqlApiIntercept = ( interceptFn ) =>
        ( operationName, variables, onRequestFn ) =>
            interceptFn(
                matchRequest( operationName, variables ),
                onRequestFn,
            );

    const graphqlApiProvide = ( provideFn ) =>
        ( operationName, variables, body ) =>
            provideFn(
                matchRequest( operationName, variables ),
                body,
                200,
                GRAPHQL_CORS_HEADERS,
            );

    return async ( t, run, ...args ) => {

        /// Ignore all OPTIONS requests:
        t.onRequest.provideAll( 'OPTIONS', URL, {}, null, GRAPHQL_CORS_HEADERS );

        return run(
            Object.assign( t, {
                [MACRO_NAME]: {
                    interceptAll:   graphqlApiIntercept(   t.onRequest.interceptAll ),
                    interceptOnce:  graphqlApiIntercept(   t.onRequest.interceptOnce ),
                    provideAll:     graphqlApiProvide(     t.onRequest.provideAll ),
                    provideOnce:    graphqlApiProvide(     t.onRequest.provideOnce ),
                    respond:        graphqlApiRespond,
                },
            }),
            ...args,
        );
    };
};
