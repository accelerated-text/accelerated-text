import test                     from 'ava';

import { createDataFileFull }   from './data/data-file';
import {
    default as DATA_FILE_LIST,
    EMPTY_DATA_FILE_LIST,
}                               from './data/data-file-list';
import DOCUMENT_PLAN_LIST       from './data/document-plan-list';

import customResponsesPage      from './lib/custom-responses-page';
import defaultResponsesPage     from './lib/default-responses-page';
import {
    isDataFileRecordVisible,
    selectDataFile,
}   from './lib/data-manager-utils';
import noRecordsPage            from './lib/no-records-page';
import { respondOnPlanChange }  from './lib/responses';
import { SELECTORS }            from './constants';


const DATA_FILES =              DATA_FILE_LIST.listDataFiles.dataFiles;


test( 'default elements visible', defaultResponsesPage, async t => {

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

    await t.notFindElement( SELECTORS.DATA_MANAGER_NO_PLAN );
});


test( 'correct elements when no plans & no files', noRecordsPage, async t => {

    await t.notFindElement( SELECTORS.DATA_MANAGER_FILE_ADD );
    await t.notFindElement( SELECTORS.DATA_MANAGER_FILE_DOWNLOAD );
    await t.notFindElement( SELECTORS.DATA_MANAGER_FILE_LIST );

    await t.notFindElement( SELECTORS.DATA_MANAGER_FILE_BROWSE );
    await t.notFindElement( SELECTORS.DATA_MANAGER_FILE_CLOSE );
    await t.notFindElement( SELECTORS.DATA_MANAGER_FILE_UPLOAD );

    await t.notFindElement( SELECTORS.DATA_MANAGER_CELL_BLOCK );
    await t.notFindElement( SELECTORS.DATA_MANAGER_CELL_NAME );
    await t.notFindElement( SELECTORS.DATA_MANAGER_CELL_TABLE );
    await t.notFindElement( SELECTORS.DATA_MANAGER_CELL_VALUE );

    await t.notFindElement( SELECTORS.DATA_MANAGER_ROW_NEXT );
    await t.notFindElement( SELECTORS.DATA_MANAGER_ROW_PREVIOUS );
    await t.notFindElement( SELECTORS.DATA_MANAGER_ROW_SELECT );

    await t.findElement( SELECTORS.DATA_MANAGER_NO_PLAN );
});


test(
    'correct elements when some plans & no files',
    customResponsesPage({
        dataFiles:              EMPTY_DATA_FILE_LIST,
        documentPlans: DOCUMENT_PLAN_LIST.map( plan => ({
            ...plan,
            dataSampleId:       null,
        })),
    }),
    async t => {

        await t.notFindElement( SELECTORS.DATA_MANAGER_FILE_ADD );
        await t.notFindElement( SELECTORS.DATA_MANAGER_FILE_DOWNLOAD );
        await t.notFindElement( SELECTORS.DATA_MANAGER_FILE_LIST );

        await t.findElement( SELECTORS.DATA_MANAGER_FILE_BROWSE );
        await t.notFindElement( SELECTORS.DATA_MANAGER_FILE_CLOSE );
        await t.findElement( SELECTORS.DATA_MANAGER_FILE_UPLOAD );

        await t.notFindElement( SELECTORS.DATA_MANAGER_CELL_BLOCK );
        await t.notFindElement( SELECTORS.DATA_MANAGER_CELL_NAME );
        await t.notFindElement( SELECTORS.DATA_MANAGER_CELL_TABLE );
        await t.notFindElement( SELECTORS.DATA_MANAGER_CELL_VALUE );

        await t.notFindElement( SELECTORS.DATA_MANAGER_ROW_NEXT );
        await t.notFindElement( SELECTORS.DATA_MANAGER_ROW_PREVIOUS );
        await t.notFindElement( SELECTORS.DATA_MANAGER_ROW_SELECT );

        await t.notFindElement( SELECTORS.DATA_MANAGER_NO_PLAN );
    },
);


test( 'can change file', defaultResponsesPage, async t => {

    const dataFile = createDataFileFull( DATA_FILES[1], {
        prefix:             t.title,
    });

    await selectDataFile( t, dataFile );

    await t.waitUntilElementGone( SELECTORS.UI_LOADING );
    await t.notFindElement( SELECTORS.UI_ERROR );

    await t.findElement( SELECTORS.DATA_MANAGER_ROW_NEXT );
    await t.findElement( SELECTORS.DATA_MANAGER_ROW_PREVIOUS );
    await t.findElement( SELECTORS.DATA_MANAGER_ROW_SELECT );

    t.is(
        await t.getElementValue( SELECTORS.DATA_MANAGER_FILE_LIST ),
        dataFile.id,
    );
    t.regex(
        await t.getElementAttribute( SELECTORS.DATA_MANAGER_FILE_DOWNLOAD, 'href' ),
        new RegExp( `${ dataFile.id }$` ),
    );
    t.is(
        await t.getElementProperty( SELECTORS.DATA_MANAGER_ROW_NEXT, 'disabled' ),
        dataFile.records.length < 2,
    );
    t.is(
        await t.getElementProperty( SELECTORS.DATA_MANAGER_ROW_PREVIOUS, 'disabled' ),
        true,
    );
    t.is(
        await t.getElementValue( SELECTORS.DATA_MANAGER_ROW_SELECT ),
        '0',
    );
});


test( 'correct cell names and values visible', defaultResponsesPage, async t => {

    const dataFile = createDataFileFull( DATA_FILES[2], {
        prefix:             t.title,
        recordCount:        5,
    });

    await selectDataFile( t, dataFile );

    await t.waitUntilElementGone( SELECTORS.UI_LOADING );
    await t.notFindElement( SELECTORS.UI_ERROR );

    await isDataFileRecordVisible( t, dataFile.records[0]);
});


test( 'can change cell value row', defaultResponsesPage, async t => {

    const dataFile = createDataFileFull( DATA_FILES[3], {
        prefix:             t.title,
        recordCount:        8,
    });

    await selectDataFile( t, dataFile );

    await t.waitUntilElementGone( SELECTORS.UI_LOADING );
    await t.notFindElement( SELECTORS.UI_ERROR );

    await isDataFileRecordVisible( t, dataFile.records[0]);

    t.page.select( SELECTORS.DATA_MANAGER_ROW_SELECT, '5' );
    await respondOnPlanChange( t );
    await isDataFileRecordVisible( t, dataFile.records[5]);

    t.page.click( SELECTORS.DATA_MANAGER_ROW_NEXT );
    await respondOnPlanChange( t );
    await isDataFileRecordVisible( t, dataFile.records[6]);

    t.page.click( SELECTORS.DATA_MANAGER_ROW_PREVIOUS );
    await respondOnPlanChange( t );
    await isDataFileRecordVisible( t, dataFile.records[5]);
});


test( 'row buttons correctly disabled', defaultResponsesPage, async t => {

    /// data file with one row:
    const dataFile1 = createDataFileFull( DATA_FILES[2], {
        prefix:             `${ t.title }-1-record`,
        recordCount:        1,
    });

    await selectDataFile( t, dataFile1 );

    await t.waitUntilElementGone( SELECTORS.UI_LOADING );
    await t.notFindElement( SELECTORS.UI_ERROR );

    await isDataFileRecordVisible( t, dataFile1.records[0]);

    t.is(
        await t.getElementProperty( SELECTORS.DATA_MANAGER_ROW_NEXT, 'disabled' ),
        true,
    );
    t.is(
        await t.getElementProperty( SELECTORS.DATA_MANAGER_ROW_PREVIOUS, 'disabled' ),
        true,
    );

    /// data file with four rows:
    const dataFile4 = createDataFileFull( DATA_FILES[3], {
        prefix:             `${ t.title }-4-records`,
        recordCount:        4,
    });

    await selectDataFile( t, dataFile4 );

    await t.waitUntilElementGone( SELECTORS.UI_LOADING );
    await t.notFindElement( SELECTORS.UI_ERROR );

    await isDataFileRecordVisible( t, dataFile4.records[0]);
    t.is(
        await t.getElementProperty( SELECTORS.DATA_MANAGER_ROW_NEXT, 'disabled' ),
        false,
    );
    t.is(
        await t.getElementProperty( SELECTORS.DATA_MANAGER_ROW_PREVIOUS, 'disabled' ),
        true,
    );

    t.page.select( SELECTORS.DATA_MANAGER_ROW_SELECT, '2' );
    await respondOnPlanChange( t );
    await isDataFileRecordVisible( t, dataFile4.records[2]);
    t.is(
        await t.getElementProperty( SELECTORS.DATA_MANAGER_ROW_NEXT, 'disabled' ),
        false,
    );
    t.is(
        await t.getElementProperty( SELECTORS.DATA_MANAGER_ROW_PREVIOUS, 'disabled' ),
        false,
    );

    t.page.click( SELECTORS.DATA_MANAGER_ROW_NEXT );
    await respondOnPlanChange( t );
    await isDataFileRecordVisible( t, dataFile4.records[3]);
    t.is(
        await t.getElementProperty( SELECTORS.DATA_MANAGER_ROW_NEXT, 'disabled' ),
        true,
    );
    t.is(
        await t.getElementProperty( SELECTORS.DATA_MANAGER_ROW_PREVIOUS, 'disabled' ),
        false,
    );

    t.page.select( SELECTORS.DATA_MANAGER_ROW_SELECT, '1' );
    await respondOnPlanChange( t );
    await isDataFileRecordVisible( t, dataFile4.records[1]);
    t.is(
        await t.getElementProperty( SELECTORS.DATA_MANAGER_ROW_NEXT, 'disabled' ),
        false,
    );
    t.is(
        await t.getElementProperty( SELECTORS.DATA_MANAGER_ROW_PREVIOUS, 'disabled' ),
        false,
    );

    t.page.click( SELECTORS.DATA_MANAGER_ROW_PREVIOUS );
    await respondOnPlanChange( t );
    await isDataFileRecordVisible( t, dataFile4.records[0]);
    t.is(
        await t.getElementProperty( SELECTORS.DATA_MANAGER_ROW_NEXT, 'disabled' ),
        false,
    );
    t.is(
        await t.getElementProperty( SELECTORS.DATA_MANAGER_ROW_PREVIOUS, 'disabled' ),
        true,
    );
});


test.todo( 'can download file' );
test.todo( 'can upload file' );
