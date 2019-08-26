import test                     from 'ava';

import { createDataFileData }   from './data/data-file-data';
import DATA_FILE_LIST           from './data/data-file-list';

import customResponsesPage      from './lib/custom-responses-page';
import defaultResponsesPage     from './lib/default-responses-page';
import {
    isDataFileRowVisible,
    selectDataFile,
}   from './lib/data-manager-utils';
import noRecordsPage            from './lib/no-records-page';
import { respondOnPlanChange }  from './lib/responses';
import { SELECTORS }            from './constants';


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
        dataFiles:              [],
    }),
    async t => {

        await t.notFindElement( SELECTORS.DATA_MANAGER_FILE_ADD );
        await t.notFindElement( SELECTORS.DATA_MANAGER_FILE_DOWNLOAD );
        await t.notFindElement( SELECTORS.DATA_MANAGER_FILE_LIST );

        await t.findElement( SELECTORS.DATA_MANAGER_FILE_BROWSE );
        await t.findElement( SELECTORS.DATA_MANAGER_FILE_CLOSE );
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

    const dataFile =        DATA_FILE_LIST[1];
    const dataFileId =      dataFile.key;
    const dataFileData =    createDataFileData({
        fieldNames:         dataFile.fieldNames,
        prefix:             t.title,
    });

    await selectDataFile( t, dataFileId, dataFileData );

    await t.waitUntilElementGone( SELECTORS.UI_LOADING );
    await t.notFindElement( SELECTORS.UI_ERROR );

    await t.findElement( SELECTORS.DATA_MANAGER_ROW_NEXT );
    await t.findElement( SELECTORS.DATA_MANAGER_ROW_PREVIOUS );
    await t.findElement( SELECTORS.DATA_MANAGER_ROW_SELECT );

    t.is(
        await t.getElementValue( SELECTORS.DATA_MANAGER_FILE_LIST ),
        dataFileId,
    );
    t.regex(
        await t.getElementAttribute( SELECTORS.DATA_MANAGER_FILE_DOWNLOAD, 'href' ),
        new RegExp( `${ dataFileId }$` ),
    );
    t.is(
        await t.getElementProperty( SELECTORS.DATA_MANAGER_ROW_NEXT, 'disabled' ),
        dataFileData.data.length < 2,
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

    const dataFile =        DATA_FILE_LIST[2];
    const dataFileData =    createDataFileData({
        fieldNames:         dataFile.fieldNames,
        prefix:             t.title,
        rowCount:           5,
    });

    await selectDataFile( t, dataFile.key, dataFileData );

    await t.waitUntilElementGone( SELECTORS.UI_LOADING );
    await t.notFindElement( SELECTORS.UI_ERROR );

    await isDataFileRowVisible( t, dataFileData.data[0]);
});


test( 'can change cell value row', defaultResponsesPage, async t => {

    const dataFile =        DATA_FILE_LIST[3];
    const dataFileData =    createDataFileData({
        fieldNames:         dataFile.fieldNames,
        prefix:             t.title,
        rowCount:           8,
    });

    await selectDataFile( t, dataFile.key, dataFileData );

    await t.waitUntilElementGone( SELECTORS.UI_LOADING );
    await t.notFindElement( SELECTORS.UI_ERROR );

    await isDataFileRowVisible( t, dataFileData.data[0]);

    t.page.select( SELECTORS.DATA_MANAGER_ROW_SELECT, '5' );
    await respondOnPlanChange( t );
    await isDataFileRowVisible( t, dataFileData.data[5]);

    t.page.click( SELECTORS.DATA_MANAGER_ROW_NEXT );
    await respondOnPlanChange( t );
    await isDataFileRowVisible( t, dataFileData.data[6]);

    t.page.click( SELECTORS.DATA_MANAGER_ROW_PREVIOUS );
    await respondOnPlanChange( t );
    await isDataFileRowVisible( t, dataFileData.data[5]);
});


test( 'row buttons correctly disabled', defaultResponsesPage, async t => {

    /// data file with one row:
    const dataFile1 =       DATA_FILE_LIST[2];
    const dataFileData1 =   createDataFileData({
        fieldNames:         dataFile1.fieldNames,
        prefix:             t.title,
        rowCount:           1,
    });

    await selectDataFile( t, dataFile1.key, dataFileData1 );

    await t.waitUntilElementGone( SELECTORS.UI_LOADING );
    await t.notFindElement( SELECTORS.UI_ERROR );

    await isDataFileRowVisible( t, dataFileData1.data[0]);

    t.is(
        await t.getElementProperty( SELECTORS.DATA_MANAGER_ROW_NEXT, 'disabled' ),
        true,
    );
    t.is(
        await t.getElementProperty( SELECTORS.DATA_MANAGER_ROW_PREVIOUS, 'disabled' ),
        true,
    );

    /// data file with four rows:
    const dataFile4 =       DATA_FILE_LIST[3];
    const dataFileData4 =   createDataFileData({
        fieldNames:         dataFile4.fieldNames,
        prefix:             t.title,
        rowCount:           4,
    });

    await selectDataFile( t, dataFile4.key, dataFileData4 );

    await t.waitUntilElementGone( SELECTORS.UI_LOADING );
    await t.notFindElement( SELECTORS.UI_ERROR );

    await isDataFileRowVisible( t, dataFileData4.data[0]);
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
    await isDataFileRowVisible( t, dataFileData4.data[2]);
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
    await isDataFileRowVisible( t, dataFileData4.data[3]);
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
    await isDataFileRowVisible( t, dataFileData4.data[1]);
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
    await isDataFileRowVisible( t, dataFileData4.data[0]);
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
