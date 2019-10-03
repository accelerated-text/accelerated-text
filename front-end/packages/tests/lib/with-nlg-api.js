export const NLG_API_CORS_HEADERS = {
    'access-control-allow-headers': 'content-type, *',
    'access-control-allow-methods': 'GET, POST, PUT, DELETE, OPTIONS',
    'access-control-allow-origin':  '*',
};


export const nlgApiUrl = url =>
    `${ process.env.ACC_TEXT_API_URL }${ url }`;

export const nlgApiRespond = ( request, body ) =>
    request.respond({
        status:         200,
        headers:        NLG_API_CORS_HEADERS,
        contentType:    'application/json',
        body: (
            typeof( body ) === 'string'
                ? body
                : JSON.stringify( body )
        ),
    });

export const nlgApiEchoBody = interceptFn => ( method, url, bodyFn = null ) =>
    interceptFn(
        method,
        nlgApiUrl( url ),
        request => nlgApiRespond( request,
            bodyFn
                ? JSON.stringify( bodyFn( JSON.parse( request.postData())))
                : request.postData()
        ),
    );

export const nlgApiIntercept = interceptFn => ( method, url, onRequestFn ) =>
    interceptFn(
        method,
        nlgApiUrl( url ),
        onRequestFn,
    );

export const nlgApiProvide = provideFn => ( method, url, body, status, headers ) =>
    provideFn(
        method,
        nlgApiUrl( url ),
        body,
        status,
        { ...NLG_API_CORS_HEADERS, ...headers }
    );

export default async ( t, run, ...args ) =>
    run(
        Object.assign( t,  {
            nlgApi: {
                echoBodyAll:    nlgApiEchoBody( t.onRequest.interceptAll ),
                echoBodyOnce:   nlgApiEchoBody( t.onRequest.interceptOnce ),
                interceptAll:   nlgApiIntercept( t.onRequest.interceptAll ),
                interceptOnce:  nlgApiIntercept( t.onRequest.interceptOnce ),
                provideAll:     nlgApiProvide( t.onRequest.provideAll ),
                provideOnce:    nlgApiProvide( t.onRequest.provideOnce ),
                respond:        nlgApiRespond,
            },
        }),
        ...args,
    );
