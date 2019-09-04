import sleep                    from 'timeout-as-promise';
import test                     from 'ava';

import defaultResponsesPage     from './lib/default-responses-page';
import DOCUMENT_PLAN_LIST       from './data/document-plan-list';
import noRecordsPage            from './lib/no-records-page';
import { SELECTORS }            from './constants';


test( 'default elements visible', defaultResponsesPage, t =>
    t.findElements( SELECTORS, {
        AMR_CONCEPT:            true,
        AMR_CONCEPT_DRAG_BLOCK: true,
        AMR_CONCEPT_LABEL:      true,
        AMR_CONCEPT_HELP:       true,
    }),
);


test( 'correct elements when no items', noRecordsPage, t =>
    t.findElements( SELECTORS, {
        AMR_CONCEPT:            false,
        AMR_CONCEPT_DRAG_BLOCK: false,
        AMR_CONCEPT_LABEL:      false,
        AMR_CONCEPT_HELP:       false,
    }),
);


test( 'can expand help text', defaultResponsesPage, async t => {

    const $help =           SELECTORS.AMR_CONCEPT_HELP;
    const $helpIcon =       SELECTORS.AMR_CONCEPT_HELP_ICON;

    const getHeight = selector =>
        t.page.$eval( selector, el => el.clientHeight );

    const heightCollapsed = await getHeight( $help );
    await t.page.click( $help );
    await sleep( 2e3 );
    const heightExpanded =  await getHeight( $help );

    t.true(
        heightExpanded > heightCollapsed,
        `Failed to expand help text (${ heightExpanded } ! > ${ heightCollapsed }).`,
    );

    await t.page.click( $helpIcon );
    await sleep( 2e3 );
    t.is( await getHeight( $help ), heightCollapsed );

    await t.page.click( $helpIcon );
    await sleep( 2e3 );
    t.is( await getHeight( $help ), heightExpanded );
});


/// Unskip this test when drag-n-drop becomes stable in Puppeteer
/// See: https://github.com/GoogleChrome/puppeteer/issues/1366
test.skip( 'can drag-in blocks', defaultResponsesPage, async t => {

    const HAS_AMR =         /type="AMR"/;
    const PLAN =            DOCUMENT_PLAN_LIST[0];
    const $blockly =        SELECTORS.BLOCKLY;
    const $dragBlock =      SELECTORS.AMR_CONCEPT_DRAG_BLOCK;

    t.notRegex( PLAN.blocklyXml, HAS_AMR, 'Cannot run test, because there are AMR blocks in the document plan.' );

    const blockCenter =     await t.getElementCenter( $dragBlock );
    t.log( blockCenter );
    await t.page.mouse.move( blockCenter.x, blockCenter.y );
    await t.page.mouse.down();

    const blocklyCenter =   await t.getElementCenter( $blockly );
    t.log( blocklyCenter );
    await t.page.mouse.move( blocklyCenter.x, blocklyCenter.y );
    await t.page.mouse.up();

    await sleep( 2e3 );

    await t.nlgApi.interceptOnce( 'PUT', `/document-plans/${ PLAN.id }`, request => {

        const body =        request.postData();
        const bodyJson =    JSON.parse( body );

        t.regex( bodyJson.blocklyXml, HAS_AMR );
        return t.nlgApi.respond( request, body );
    });
});
