import { applyTo, equals }  from 'ramda';


const URL =                 process.env.GRAPHQL_URL;


export const GRAPHQL_CORS_HEADERS = {
    'access-control-allow-headers': 'content-type, *',
    'access-control-allow-methods': 'POST, OPTIONS',
    'access-control-allow-origin':  '*',
};


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


export const graphQLIntercept = ( interceptFn ) =>
    ( operationName, variables, onRequestFn ) =>
        interceptFn(
            matchRequest( operationName, variables ),
            onRequestFn,
        );


export const graphQLProvide = ( provideFn ) =>
    ( operationName, variables, body ) =>
        provideFn(
            matchRequest( operationName, variables ),
            body,
            200,
            GRAPHQL_CORS_HEADERS,
        );


export default async ( t, run, ...args ) => {

    /// Ignore all OPTIONS requests:
    t.onRequest.provideAll( 'OPTIONS', URL, {}, null, GRAPHQL_CORS_HEADERS );

    return run(
        Object.assign( t, {
            graphQL: {
                interceptAll:   graphQLIntercept(   t.onRequest.interceptAll ),
                interceptOnce:  graphQLIntercept(   t.onRequest.interceptOnce ),
                provideAll:     graphQLProvide(     t.onRequest.provideAll ),
                provideOnce:    graphQLProvide(     t.onRequest.provideOnce ),
                respond:        graphQLRespond,
            },
        }),
        ...args,
    );
};
