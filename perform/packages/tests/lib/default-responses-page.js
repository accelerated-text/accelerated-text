import addPageAssertions    from './add-page-assertions';
import defaultResponses     from './default-responses';
import withPage             from './with-page';


export default ( t, run, ...args ) =>
    withPage( t, addPageAssertions, defaultResponses, run, ...args );
