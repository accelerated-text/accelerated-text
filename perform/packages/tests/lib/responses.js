import DOCUMENT_PLAN_LIST   from '../data/document-plan-list';
import NLG_JOB              from '../data/nlg-job';
import NLG_JOB_RESULT       from '../data/nlg-job-result';


export const respondOnPlanChange = ( t, {
    documentPlan =          DOCUMENT_PLAN_LIST[0],
} = {}) => {
    const planUrl =         `/document-plans/${ documentPlan.id }`;

    return t.nlgApi.provideOnce( 'OPTIONS', planUrl, null )
        .then(() => t.nlgApi.echoBodyOnce( 'PUT', planUrl ))
        .then(() => t.nlgApi.provideOnce( 'OPTIONS', '/nlg/', '' ))
        .then(() => t.nlgApi.provideOnce( 'POST', '/nlg/', NLG_JOB ))
        .then(() => t.nlgApi.provideOnce( 'GET', `/nlg/${ NLG_JOB.resultId }`, NLG_JOB_RESULT ));
};
