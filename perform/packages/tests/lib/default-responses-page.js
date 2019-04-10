import debugConsole         from '../../qa-utils/debug-console';

import addPageAssertions    from './add-page-assertions';
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
