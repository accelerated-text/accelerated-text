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


test.todo( 'can expand help text' );
test.todo( 'can drag-in blocks' );
