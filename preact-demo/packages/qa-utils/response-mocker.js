module.exports = ( page, prefix ) => {

    let expectMethod =      '';
    let expectUrl =         '';
    let provideResponse =   null;
    let rejectFn =          null;
    let resolveFn =         null;

    const resetState = () => {
        expectMethod =      '';
        expectUrl =         '';
        provideResponse =   null;
        rejectFn =          null;
        resolveFn =         null;
    };

    const onRequest = async request => {
        const method =      request.method();
        const url =         request.url();

        if( !url.startsWith( prefix )) {
            return request.continue();
        } else if( !resolveFn || !rejectFn ) {
            throw Error( `Got unexpected request for ${ method } ${ url }.` );
        } else if( method !== expectMethod || url !== expectUrl ) {
            rejectFn( `Expected: ${ expectMethod } ${ expectUrl }.\n Got: ${ method } ${ url }.` );
            resetState();
        } else if( provideResponse ) {
            await request.respond( provideResponse );
            resolveFn( request ); ///.respond( provideResponse ));
            resetState();
        } else {
            await request.continue();
            resolveFn( request ); ///.continue());
            resetState();
        }
    };

    const continueRequest =
        ( method, path ) =>
            new Promise(( resolve, reject ) => {
                expectMethod =      method;
                expectUrl =         `${ prefix }${ path }`;
                provideResponse =   null;
                rejectFn =          reject;
                resolveFn =         resolve;
            });

    const mockResponse =
        ( method, path, bodyData, status = 200, headers = {}) =>
            new Promise(( resolve, reject ) => {
                expectMethod =      method;
                expectUrl =         `${ prefix }${ path }`;
                rejectFn =          reject;
                resolveFn =         resolve;

                const body = (
                    headers.contentType
                        ? bodyData
                        : JSON.stringify( bodyData )
                );
                provideResponse = {
                    body,
                    contentType:    headers.contentType || 'application/json',
                    headers,
                    status,
                };
            });

    const startMocker = async () => {
        resetState();
        await page.setRequestInterception( true );
        return page.on( 'request', onRequest );
    };

    const stopMocker = () => {
        resetState();
        page.removeListener( 'request', onRequest );
        return page.setRequestInterception( false );
    };

    return {
        continueRequest,
        mockResponse,
        startMocker,
        stopMocker,
    };
};
