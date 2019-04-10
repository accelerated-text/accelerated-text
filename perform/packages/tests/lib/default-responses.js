import DATA_FILE            from '../data/data-file';
import DOCUMENT_PLAN_LIST   from '../data/document-plan-list';
import LEXICON_LIST         from '../data/lexicon-list';
import NLG_JOB              from '../data/nlg-job';
import NLG_JOB_RESULT       from '../data/nlg-job-result';
import USER                 from '../data/user';


const { TEST_URL } =        process.env;


export default async ( t, run, ...args ) => {

    const {
        interceptor: { continueAll },
        nlgProvideOnce,
    } = t;

    continueAll( 'GET', new RegExp( `${ TEST_URL }/.*` ));

    /// Start page load:
    t.timeout( 8e3 );
    const pageLoadResult =  t.page.goto( TEST_URL, { timeout: 8e3 })
        .then( t.pass, t.fail );

    /// Register these intercepts while the page is loading:
    await Promise.all([
        nlgProvideOnce( 'GET', `/data/?user=${ USER.id }`, [ DATA_FILE ]),
        nlgProvideOnce( 'GET', '/lexicon?', LEXICON_LIST ),
        nlgProvideOnce( 'GET', '/document-plans/', DOCUMENT_PLAN_LIST )
            .then(() => nlgProvideOnce( 'POST', '/nlg/', NLG_JOB ))
            .then(() => nlgProvideOnce( 'GET', `/nlg/${ NLG_JOB.resultId }`, NLG_JOB_RESULT )),
    ]);

    await pageLoadResult;

    if( run ) {
        await run( t, ...args );
    }
};
