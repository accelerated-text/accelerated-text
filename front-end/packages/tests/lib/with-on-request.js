import puppeteerOnRequest   from '../../puppeteer-on-request/';


export default async ( t, run, ...args ) => {

    const onRequest =       await puppeteerOnRequest( t.page, {
        onError:            err => {
            t.log(
                err.method,
                err.url,
                err.method === 'POST' ? `\n${ err.request.postData() }` : '',
            );
            return t.fail( err );
        },
        /// onRequest:  ({ method, url }) => t.log( `onRequest ${ method } ${ url }` ),
    });

    if( run ) {
        await run( Object.assign( t, { onRequest }), ...args );
    }

    await onRequest.stopInterception( t.page );
};
