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
    async onRequestFn => {

        await provideFn( 'OPTIONS', URL, {});
        return interceptFn( 'POST', URL, onRequestFn );
    };


export const graphQLProvide = provideFn =>
    async ( body, status, headers ) => {

        const allHeaders =  graphQLHeaders( headers );

        await provideFn( 'OPTIONS', URL, {}, status, allHeaders );
        return provideFn( 'POST', URL, body, status, allHeaders );
    };


export default async ( t, run, ...args ) =>
    run(
        Object.assign( t, {
            graphQL: {
                intercept:      graphQLIntercept( t.interceptor.intercept,      t.interceptor.provide ),
                interceptAll:   graphQLIntercept( t.interceptor.interceptAll,   t.interceptor.provideAll ),
                interceptOnce:  graphQLIntercept( t.interceptor.interceptOnce,  t.interceptor.provideOnce ),
                provide:        graphQLProvide( t.interceptor.provide ),
                provideAll:     graphQLProvide( t.interceptor.provideAll ),
                provideOnce:    graphQLProvide( t.interceptor.provideOnce ),
                respond:        graphQLRespond,
            },
        }),
        ...args,
    );
