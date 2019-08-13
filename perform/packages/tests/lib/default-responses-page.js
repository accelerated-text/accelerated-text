import addPageUtils         from './add-page-utils';
import debugConsole         from './debug-console';
import defaultResponses     from './default-responses';
import withDefaultTimeout   from './with-default-timeout';
import withFakeShopApi      from './with-fake-shop-api';
import withGraphqlApi       from './with-graphql-api';
import withNlgApi           from './with-nlg-api';
import withOnRequest        from './with-on-request';
import withPage             from './with-page';
import withSharedBrowser    from './with-shared-browser';


export default ( t, run, ...args ) =>
    withSharedBrowser(
        t,
        withPage,
        debugConsole,
        addPageUtils,
        withOnRequest,
        withFakeShopApi,
        withGraphqlApi,
        withNlgApi,
        defaultResponses,
        withDefaultTimeout,
        run,
        ...args,
    );
