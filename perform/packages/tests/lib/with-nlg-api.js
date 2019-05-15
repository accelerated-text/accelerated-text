export const NLG_API_CORS_HEADERS = {
    'access-control-allow-methods': 'GET, POST, PUT, DELETE, OPTIONS',
    'access-control-allow-origin':  '*',
};


export const nlgApiUrl = url =>
    `${ process.env.NLG_API_URL }${ url }`;

export const nlgApiEchoBody = interceptFn => ( method, url, bodyFn = null ) =>
    interceptFn(
        method,
        nlgApiUrl( url ),
        request => request.respond({
            status:         200,
            headers:        NLG_API_CORS_HEADERS,
            contentType:    'application/json',
            body: (
                bodyFn
                    ? JSON.stringify( bodyFn( JSON.parse( request.postData())))
                    : request.postData()
            ),
        }),
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
                echoBody:       nlgApiEchoBody( t.interceptor.intercept ),
                echoBodyAll:    nlgApiEchoBody( t.interceptor.interceptAll ),
                echoBodyOnce:   nlgApiEchoBody( t.interceptor.interceptOnce ),
                provide:        nlgApiProvide( t.interceptor.provide ),
                provideAll:     nlgApiProvide( t.interceptor.provideAll ),
                provideOnce:    nlgApiProvide( t.interceptor.provideOnce ),
            },
        }),
        ...args,
    );
