import requestInterceptor   from '../../qa-utils/request-interceptor';


export default async ( t, run, ...args ) => {

    const interceptor =     await requestInterceptor( t.page, {
        onError:            t.fail,
        /// onRequest:  ({ method, url }) => t.log( `onRequest ${ method } ${ url }` ),
    });

    if( run ) {
        await run( Object.assign( t, { interceptor }), ...args );
    }

    await interceptor.stopInterception( t.page );
};
