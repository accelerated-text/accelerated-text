import test             from 'ava';

import noRecords        from './response-templates/no-records';
import withPage         from './with-page';


test( 'should render logo', withPage, async ( t, page ) => {
    t.timeout( 5e3 );

    await noRecords( page );
    await t.findElement( page, 'img[title="Accelerated Text"]' );
});
