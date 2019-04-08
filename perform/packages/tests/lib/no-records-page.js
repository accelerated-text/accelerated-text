import addPageAssertions    from './add-page-assertions';
import noRecords            from './no-records';
import withPage             from './with-page';


export default ( t, run, ...args ) =>
    withPage( t, addPageAssertions, noRecords, run, ...args );
