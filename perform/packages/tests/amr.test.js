import test                     from 'ava';

import defaultResponsesPage     from './lib/default-responses-page';
import noRecordsPage            from './lib/no-records-page';
import { SELECTORS }            from './constants';


test( 'default elements visible', defaultResponsesPage, async t => {

    await t.findElement( SELECTORS.AMR_CONCEPT );
});


test( 'correct elements when no items', noRecordsPage, async t => {

    await t.notFindElement( SELECTORS.AMR_CONCEPT );
});

test.todo( 'can expand help text' );
test.todo( 'can drag-in blocks' );
