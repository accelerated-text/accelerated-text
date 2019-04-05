import test             from 'ava';

import noRecords        from './lib/no-records';
import { SELECTORS }    from './constants';
import withPage         from './lib/with-page';


test( 'should not have errors', withPage, async ( t, page ) => {
    t.timeout( 5e3 );

    await noRecords( page );
    await t.notFindElement( page, SELECTORS.UI_ERROR );
});


test( 'should handle empty plan list', withPage, async ( t, page ) => {
    t.timeout( 5e3 );

    await noRecords( page );
    await t.findElement( page, SELECTORS.BTN_NEW_PLAN );
});
