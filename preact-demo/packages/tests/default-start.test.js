import test             from 'ava';

import defaultResponses from './lib/default-responses';
import DOCUMENT_PLAN    from './data/document-plan';
import { SELECTORS }    from './constants';
import withPage         from './lib/with-page';


test( 'should not have errors', withPage, async ( t, page ) => {
    t.timeout( 5e3 );

    await defaultResponses( page );
    await t.notFindElement( page, SELECTORS.UI_ERROR );
});


test( 'should load the document plan', withPage, async ( t, page ) => {
    t.timeout( 5e3 );

    await defaultResponses( page );
    await t.findElement( page, `[data-id=${ DOCUMENT_PLAN.documentPlan.srcId }]` );
    await t.findElement( page, `[data-id=${ DOCUMENT_PLAN.documentPlan.segments[0].srcId }]` );
});


test( 'should load a variant', withPage, async ( t, page ) => {
    t.timeout( 5e3 );

    await defaultResponses( page );
    await t.findElement( page, SELECTORS.VARIANT );
});
