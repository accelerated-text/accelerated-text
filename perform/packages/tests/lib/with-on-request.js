import puppeteerOnRequest   from '../../puppeteer-on-request/';


export default async ( t, run, ...args ) => {

    const onRequest =       await puppeteerOnRequest( t.page, {
        onError:            t.fail,
        /// onRequest:  ({ method, url }) => t.log( `onRequest ${ method } ${ url }` ),
    });

    if( run ) {
        await run( Object.assign( t, { onRequest }), ...args );
    }

    await onRequest.stopInterception( t.page );
};
