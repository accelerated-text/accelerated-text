const URL =             process.env.GRAPHQL_URL;


export const GRAPHQL_CORS_HEADERS = {
    'access-control-allow-headers': 'content-type, *',
    'access-control-allow-methods': 'POST, OPTIONS',
    'access-control-allow-origin':  '*',
};


export const graphQLHeaders = headers => ({
    ...GRAPHQL_CORS_HEADERS,
    ...headers,
});


export const graphQLRespond = ( request, body ) =>
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


export const graphQLIntercept = ( interceptFn, provideFn ) =>
    onRequestFn =>
        interceptFn( 'POST', URL, onRequestFn );


export const graphQLProvide = ( t, interceptFn ) =>
    ( operationName, variables, body ) =>
        interceptFn( 'POST', URL, request => {
            const postData =    JSON.parse( request.postData());
            t.deepEqual( postData.operationName, operationName );
            t.deepEqual( postData.variables, variables );
            return graphQLRespond( request, body );
        });


export default async ( t, run, ...args ) => {

    /// Ignore all OPTIONS requests:
    t.interceptor.provideAll( 'OPTIONS', URL, {}, null, GRAPHQL_CORS_HEADERS );

    return run(
        Object.assign( t, {
            graphQL: {
                intercept:      graphQLIntercept( t.interceptor.intercept,      t.interceptor.provide ),
                interceptAll:   graphQLIntercept( t.interceptor.interceptAll,   t.interceptor.provideAll ),
                interceptOnce:  graphQLIntercept( t.interceptor.interceptOnce,  t.interceptor.provideOnce ),
                provide:        graphQLProvide( t, t.interceptor.intercept ),
                provideAll:     graphQLProvide( t, t.interceptor.interceptAll ),
                provideOnce:    graphQLProvide( t, t.interceptor.interceptOnce ),
                respond:        graphQLRespond,
            },
        }),
        ...args,
    );
};
