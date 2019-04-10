import addPageAssertions    from './add-page-assertions';
import debugConsole         from './debug-console';
import defaultResponses     from './default-responses';
import withInterceptor      from './with-interceptor';
import withPage             from './with-page';


export default ( t, run, ...args ) =>
    withPage(
        t,
        debugConsole,
        addPageAssertions,
        withInterceptor,
        defaultResponses,
        run,
        ...args,
    );
