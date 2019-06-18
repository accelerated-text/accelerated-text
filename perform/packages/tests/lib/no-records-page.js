import addPageUtils         from './add-page-utils';
import debugConsole         from './debug-console';
import noRecords            from './no-records';
import withGraphQL          from './with-graphql';
import withNlgApi           from './with-nlg-api';
import withOnRequest        from './with-on-request';
import withPage             from './with-page';


export default ( t, run, ...args ) =>
    withPage(
        t,
        debugConsole,
        addPageUtils,
        withOnRequest,
        withGraphQL,
        withNlgApi,
        noRecords,
        run,
        ...args,
    );
