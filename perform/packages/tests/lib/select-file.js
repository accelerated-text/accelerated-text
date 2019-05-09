import DOCUMENT_PLAN_LIST   from '../data/document-plan-list';
import NLG_JOB              from '../data/nlg-job';
import NLG_JOB_RESULT       from '../data/nlg-job-result';
import { SELECTORS }        from '../constants';


export default ( t, fileId, fileData ) => {

    const PLAN_URL =        `/document-plans/${ DOCUMENT_PLAN_LIST[0].id }`;

    t.page.select( SELECTORS.DATA_MANAGER_FILE_LIST, fileId );

    return Promise.all([
        t.nlgApi.provideOnce( 'GET', `/data/${ fileId }`, fileData ),
        t.nlgApi.provideOnce( 'OPTIONS', PLAN_URL, null )
            .then(() => t.nlgApi.echoBodyOnce( 'PUT', PLAN_URL ))
            .then(() => t.nlgApi.provideOnce( 'POST', '/nlg/', NLG_JOB ))
            .then(() => t.nlgApi.provideOnce( 'GET', `/nlg/${ NLG_JOB.resultId }`, NLG_JOB_RESULT )),
    ]);
};

