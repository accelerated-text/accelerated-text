import DOCUMENT_PLAN_LIST   from '../data/document-plan-list';
import NLG_JOB              from '../data/nlg-job';
import NLG_JOB_RESULT       from '../data/nlg-job-result';


export const respondOnPlanChange = (
    t,
    patch =                 null,
    documentPlan =          DOCUMENT_PLAN_LIST.documentPlans.items[0]
) => {
    const plan = {
        ...documentPlan,
        ...patch,
    };

    return t.graphqlApi.provideOnce(
        'updateDocumentPlan',
        plan,
        {
            data: {
                updateDocumentPlan: {
                    ...plan,
                    updateCount:    plan.updateCount + 1,
                },
            },
        },
    )
        .then(() => t.nlgApi.provideOnce( 'OPTIONS', '/nlg/', '' ))
        .then(() => t.nlgApi.provideOnce( 'POST', '/nlg/', NLG_JOB ))
        .then(() => t.nlgApi.provideOnce( 'GET', `/nlg/${ NLG_JOB.resultId }`, NLG_JOB_RESULT ));
};
