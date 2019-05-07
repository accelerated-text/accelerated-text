import addPageAssertions    from './add-page-assertions';
import debugConsole         from './debug-console';
import noRecords            from './no-records';
import withInterceptor      from './with-interceptor';
import withNlgApi           from './with-nlg-api';
import withPage             from './with-page';


export default ( t, run, ...args ) =>
    withPage(
        t,
        debugConsole,
        addPageAssertions,
        withInterceptor,
        withNlgApi,
        noRecords,
        run,
        ...args,
    );
