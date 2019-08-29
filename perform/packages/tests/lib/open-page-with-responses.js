import CONCEPTS                 from '../data/concepts';
import DATA_FILE_LIST           from '../data/data-file-list';
import DOCUMENT_PLAN_LIST       from '../data/document-plan-list';
import DICTIONARY               from '../data/dictionary';
import NLG_JOB                  from '../data/nlg-job';
import NLG_JOB_RESULT           from '../data/nlg-job-result';
import READER_FLAGS             from '../data/reader-flags';

import pageResponses            from './page-responses';

const { TEST_URL } =            process.env;


export default responses => async ( t, run, ...args ) => {

    t.onRequest.continueAll( 'GET', new RegExp( `${ TEST_URL }/.*` ));

    /// Start page load:
    t.timeout( 8e3 );
    const pageLoadResult =  t.page.goto( TEST_URL, { timeout: 8e3 })
        .then( t.pass, t.fail );

    /// Register these intercepts while the page is loading:
    await pageResponses( t, {
        concepts:               CONCEPTS,
        dataFiles:              DATA_FILE_LIST,
        dictionary:             DICTIONARY,
        documentPlans:          DOCUMENT_PLAN_LIST,
        nlgJob:                 NLG_JOB,
        nlgJobResult:           NLG_JOB_RESULT,
        readerFlags:            READER_FLAGS,
        ...responses,
    });

    await pageLoadResult;

    if( run ) {
        await run( t, ...args );
    }
};
