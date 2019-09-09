import uuid             from 'uuid';

import planTemplate     from './plan-template';


export const createPlan = fields => ({
    ...planTemplate,
    ...fields,
    createdAt:          +new Date,
    id:                 undefined,
    uid:                uuid.v4(),
});

export const fixPlan = fields => ({
    ...planTemplate,
    ...fields,
    createdAt:          fields.createdAt || 0,
    uid:                fields.uid || fields.id || uuid.v4(),
});

export const isSamePlan = ( p1, p2 ) => (
    p1.uid === p2.uid
    && p1.id === p2.id
    && p1.updateCount === p2.updateCount
    && p1.name === p2.name
    && p1.blocklyXml === p2.blocklyXml
    && JSON.stringify( p1.documentPlan ) === JSON.stringify( p2.documentPlan )
);


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
