import addPageUtils         from './add-page-utils';
import debugConsole         from './debug-console';
import defaultResponses     from './default-responses';
import withGraphqlApi       from './with-graphql-api';
import withNlgApi           from './with-nlg-api';
import withOnRequest        from './with-on-request';
import withPage             from './with-page';


export default ( t, run, ...args ) =>
    withPage(
        t,
        debugConsole,
        addPageUtils,
        withOnRequest,
        withGraphqlApi,
        withNlgApi,
        defaultResponses,
        run,
        ...args,
    );
