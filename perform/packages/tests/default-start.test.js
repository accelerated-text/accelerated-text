import test                 from 'ava';

import defaultResponsesPage from './lib/default-responses-page';
import DOCUMENT_PLAN        from './data/document-plan';
import { SELECTORS }        from './constants';


test( 'should not have errors', defaultResponsesPage, async t => {
    t.timeout( 5e3 );

    await t.notFindElement( SELECTORS.UI_ERROR );
});


test( 'should load the document plan', defaultResponsesPage, async t => {
    t.timeout( 5e3 );

    await t.findElement( `[data-id=${ DOCUMENT_PLAN.documentPlan.srcId }]` );
    await t.findElement( `[data-id=${ DOCUMENT_PLAN.documentPlan.segments[0].srcId }]` );
});


test( 'should load a variant', defaultResponsesPage, async t => {
    t.timeout( 5e3 );

    await t.findElement( SELECTORS.VARIANT );
});
