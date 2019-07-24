import sleep                    from 'timeout-as-promise';
import test                     from 'ava';

import defaultResponsesPage     from './lib/default-responses-page';
import noRecordsPage            from './lib/no-records-page';
import { SELECTORS }            from './constants';


test( 'default elements visible', defaultResponsesPage, async t => {

    await t.findElement( SELECTORS.AMR_CONCEPT );
    await t.findElement( SELECTORS.AMR_CONCEPT_DRAG_BLOCK );
    await t.findElement( SELECTORS.AMR_CONCEPT_LABEL );
    await t.findElement( SELECTORS.AMR_CONCEPT_HELP );
});


test( 'correct elements when no items', noRecordsPage, async t => {

    await t.notFindElement( SELECTORS.AMR_CONCEPT );
    await t.notFindElement( SELECTORS.AMR_CONCEPT_DRAG_BLOCK );
    await t.notFindElement( SELECTORS.AMR_CONCEPT_LABEL );
    await t.notFindElement( SELECTORS.AMR_CONCEPT_HELP );
});


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


test.todo( 'can drag-in blocks' );
