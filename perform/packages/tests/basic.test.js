import test             from 'ava';

import noRecordsPage    from './lib/no-records-page';


test( 'should render logo', noRecordsPage, async t => {

    await t.findElement( 'img[title="Accelerated Text"]' );
});
