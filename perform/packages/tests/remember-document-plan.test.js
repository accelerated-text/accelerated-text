import sleep                from 'timeout-as-promise';
import test                 from 'ava';

import debugConsole         from '../qa-utils/debug-console';
import { OPENED_PLAN_UID }  from '../plan-list/local-storage-adapter';

import addPageAssertions    from './lib/add-page-assertions';
import defaultResponses     from './lib/default-responses';
import DOCUMENT_PLAN_LIST   from './data/document-plan-list';
import noRecords            from './lib/no-records';
import withInterceptor      from './lib/with-interceptor';
import withPage             from './lib/with-page';

import { SELECTORS }        from './constants';


test(
    'should open the remembered document plan',
    withPage,
    debugConsole,
    addPageAssertions,
    withInterceptor,
    async t => {

        const PLAN =            DOCUMENT_PLAN_LIST[1];

        /// Need to open the page before accessing localStorage:
        await noRecords( t );
        await t.page.evaluate(
            ( key, value ) => localStorage.setItem( key, value ),
            OPENED_PLAN_UID,
            PLAN.uid,
        );

        await defaultResponses( t );

        t.timeout( 8e3 );
        await t.findElement( `[data-id=${ PLAN.documentPlan.srcId }]` );
        await t.findElement( `[data-id=${ PLAN.documentPlan.segments[0].srcId }]` );

        /// Wait to make sure no unnecessary requests sent:
        t.timeout( 10e3 );
        await sleep( 8e3 );
        await t.notFindElement( SELECTORS.UI_ERROR );
    }
);
