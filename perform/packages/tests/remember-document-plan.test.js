import sleep                from 'timeout-as-promise';
import test                 from 'ava';

import { OPENED_PLAN_UID }  from '../plan-list/local-storage-adapter';

import addPageUtils         from './lib/add-page-utils';
import debugConsole         from './lib/debug-console';
import defaultResponses     from './lib/default-responses';
import DOCUMENT_PLAN_LIST   from './data/document-plan-list';
import noRecords            from './lib/no-records';
import withBrowser          from './lib/with-browser';
import withFakeShopApi      from './lib/with-fake-shop-api';
import withGraphqlApi       from './lib/with-graphql-api';
import withOnRequest        from './lib/with-on-request';
import withNlgApi           from './lib/with-nlg-api';
import withPage             from './lib/with-page';

import { SELECTORS }        from './constants';


test(
    'should open the remembered document plan',
    withBrowser,
    withPage,
    debugConsole,
    addPageUtils,
    withOnRequest,
    withFakeShopApi,
    withGraphqlApi,
    withNlgApi,
    async t => {

        const PLAN =            DOCUMENT_PLAN_LIST[1];

        /// Need to open the page before accessing localStorage:
        await noRecords( t );
        t.timeout( 16e3 );
        await t.waitUntilElementGone( SELECTORS.UI_LOADING );

        await t.page.evaluate(
            ( key, value ) => localStorage.setItem( key, value ),
            OPENED_PLAN_UID,
            PLAN.uid,
        );

        await defaultResponses( t );
        await t.waitUntilElementGone( SELECTORS.UI_LOADING );

        await t.findElement( `[data-id=${ PLAN.documentPlan.srcId }]` );
        await t.findElement( `[data-id=${ PLAN.documentPlan.segments[0].srcId }]` );
        await t.findElement( SELECTORS.VARIANT );

        /// Wait to make sure no unnecessary requests sent:
        t.timeout( 10e3 );
        await sleep( 8e3 );
        await t.notFindElement( SELECTORS.UI_ERROR );
    }
);
