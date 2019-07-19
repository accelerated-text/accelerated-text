import addPageUtils         from './add-page-utils';
import debugConsole         from './debug-console';
import defaultResponses     from './default-responses';
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
        withGraphqlApi,
        withNlgApi,
        defaultResponses,
        run,
        ...args,
    );
