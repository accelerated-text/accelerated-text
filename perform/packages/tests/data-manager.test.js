import test                 from 'ava';

import DATA_FILE_DATA       from './data/data-file-data';
import DATA_FILE_LIST       from './data/data-file-list';
import defaultResponsesPage from './lib/default-responses-page';
import DOCUMENT_PLAN_LIST   from './data/document-plan-list';
import { SELECTORS }        from './constants';


test( 'default elements visible', defaultResponsesPage, async t => {
    t.timeout( 5e3 );

    await t.findElement( SELECTORS.DATA_MANAGER_FILE_ADD );
    await t.findElement( SELECTORS.DATA_MANAGER_FILE_DOWNLOAD );
    await t.findElement( SELECTORS.DATA_MANAGER_FILE_LIST );

    await t.notFindElement( SELECTORS.DATA_MANAGER_FILE_BROWSE );
    await t.notFindElement( SELECTORS.DATA_MANAGER_FILE_CLOSE );
    await t.notFindElement( SELECTORS.DATA_MANAGER_FILE_UPLOAD );

    await t.findElement( SELECTORS.DATA_MANAGER_CELL_BLOCK );
    await t.findElement( SELECTORS.DATA_MANAGER_CELL_NAME );
    await t.findElement( SELECTORS.DATA_MANAGER_CELL_TABLE );
    await t.findElement( SELECTORS.DATA_MANAGER_CELL_VALUE );

    await t.findElement( SELECTORS.DATA_MANAGER_ROW_NEXT );
    await t.findElement( SELECTORS.DATA_MANAGER_ROW_PREVIOUS );
    await t.findElement( SELECTORS.DATA_MANAGER_ROW_SELECT );
});


test.todo( 'correct elements when no files' );


test( 'can change file', defaultResponsesPage, async t => {
    t.timeout( 5e3 );

    const DATA_FILE_ID =    DATA_FILE_LIST[2].key;
    const PLAN_URL =        `/document-plans/${ DOCUMENT_PLAN_LIST[0].id }`;

    const updateRequests = (
        t.nlgApi.provideOnce( 'OPTIONS', PLAN_URL, null )
            .then(() => Promise.all([
                t.nlgApi.echoBodyOnce( 'PUT', PLAN_URL ),
                t.nlgApi.provideOnce( 'GET', `/data/${ DATA_FILE_ID }`, DATA_FILE_DATA ),
            ]))
    );

    await t.page.select( SELECTORS.DATA_MANAGER_FILE_LIST, DATA_FILE_ID );

    await updateRequests;

    const value = await t.page.evaluate(
        selector => document.querySelector( selector ).value,
        SELECTORS.DATA_MANAGER_FILE_LIST,
    );
    t.is( value, DATA_FILE_ID, 'Failed to change file in select.' );
});


test.todo( 'can download file' );
test.todo( 'can upload file' );

test.todo( 'correct number of cells visible' );
test.todo( 'correct cell names visible' );
test.todo( 'correct cell values visible' );

test.todo( 'can change cell value row' );
test.todo( 'row buttons correctly disabled' );
