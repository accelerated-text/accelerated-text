import test             from 'ava';

import noRecordsPage    from './lib/no-records-page';
import { SELECTORS }    from './constants';


test( 'should not have errors', noRecordsPage, async t => {

    await t.notFindElement( SELECTORS.UI_ERROR );
});


test( 'should handle empty plan list', noRecordsPage, async t => {

    await t.findElement( SELECTORS.BTN_NEW_PLAN );
});
