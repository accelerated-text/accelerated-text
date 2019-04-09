import debugConsole         from '../../qa-utils/debug-console';
import nlgProvide           from '../../nlg-api/provide-response';
import requestInterceptor   from '../../qa-utils/request-interceptor';

import DATA_FILE            from '../data/data-file';
import DOCUMENT_PLAN_LIST   from '../data/document-plan-list';
import LEXICON_LIST         from '../data/lexicon-list';
import NLG_JOB              from '../data/nlg-job';
import NLG_JOB_RESULT       from '../data/nlg-job-result';
import USER                 from '../data/user';


const { TEST_URL } =        process.env;


export default async ( t, run, ...args ) => {

    debugConsole( t.page );

    const interceptor =     await requestInterceptor( t.page );
    const {
        continueAll,
        provideOnce,
        stopInterception,
    } = interceptor;
    const nlgProvideOnce =  nlgProvide( provideOnce );

    continueAll( 'GET', new RegExp( `${ TEST_URL }/.*` ));

    t.page.goto( TEST_URL );

    await Promise.all([
        nlgProvideOnce( 'GET', `/data/?user=${ USER.id }`, [ DATA_FILE ]),
        nlgProvideOnce( 'GET', '/lexicon?', LEXICON_LIST ),
        nlgProvideOnce( 'GET', '/document-plans/', DOCUMENT_PLAN_LIST )
            .then(() => nlgProvideOnce( 'POST', '/nlg/', NLG_JOB ))
            .then(() => nlgProvideOnce( 'GET', `/nlg/${ NLG_JOB.resultId }`, NLG_JOB_RESULT )),
    ]);

    if( run ) {
        await run( Object.assign( t, { interceptor, nlgProvideOnce }), ...args );
    }

    await stopInterception( t.page );
};
