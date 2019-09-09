import uuid             from 'uuid';

import planTemplate     from './plan-template';


export const createPlan = fields => ({
    ...planTemplate,
    ...fields,
    createdAt:          +new Date,
    id:                 undefined,
    uid:                uuid.v4(),
});


export const preparePlanJson = plan => ({
    ...plan,
    documentPlan: (
        typeof plan.documentPlan === 'string'
            ? plan.documentPlan
            : JSON.stringify( plan.documentPlan )
    ),
});


export const getPlanByUid = ( documentPlans, uid ) => (
    uid
    && documentPlans
    && documentPlans.items
    && documentPlans.items.find(
        item => item.uid === uid
    )
);
