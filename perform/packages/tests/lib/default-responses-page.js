import addPageUtils         from './add-page-utils';
import debugConsole         from './debug-console';
import defaultResponses     from './default-responses';
import withGraphQL          from './with-graphql';
import withInterceptor      from './with-interceptor';
import withNlgApi           from './with-nlg-api';
import withPage             from './with-page';


export default ( t, run, ...args ) =>
    withPage(
        t,
        debugConsole,
        addPageUtils,
        withInterceptor,
        withGraphQL,
        withNlgApi,
        defaultResponses,
        run,
        ...args,
    );
