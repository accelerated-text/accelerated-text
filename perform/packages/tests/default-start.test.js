import sleep                from 'timeout-as-promise';
import test                 from 'ava';

import defaultResponsesPage from './lib/default-responses-page';
import DOCUMENT_PLAN_LIST   from './data/document-plan-list';
import { SELECTORS }        from './constants';


test( 'should not have errors', defaultResponsesPage, async t => {
    t.timeout( 5e3 );

    await t.notFindElement( SELECTORS.UI_ERROR );
});


test( 'should not start unaccounted-for requests', defaultResponsesPage, async t => {
    t.timeout( 10e3 );

    await sleep( 8e3 );
    await t.notFindElement( SELECTORS.UI_ERROR );
});


test( 'should load the document plan', defaultResponsesPage, async t => {
    t.timeout( 5e3 );

    const PLAN =            DOCUMENT_PLAN_LIST[0];

    await t.findElement( `[data-id=${ PLAN.documentPlan.srcId }]` );
    await t.findElement( `[data-id=${ PLAN.documentPlan.segments[0].srcId }]` );
});


test( 'should load a variant', defaultResponsesPage, async t => {
    t.timeout( 5e3 );

    await t.findElement( SELECTORS.VARIANT );
});
