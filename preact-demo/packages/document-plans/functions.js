import uuid             from 'uuid';

import planTemplate     from './plan-template';


export const createPlan = fields => ({
    ...planTemplate,
    ...fields,
    createdAt:          +new Date,
    uid:                uuid.v4(),
});

export const fixPlan = fields => ({
    ...planTemplate,
    ...fields,
    createdAt:          fields.createdAt || 0,
    uid:                fields.uid || fields.id || uuid.v4(),
});

export const getPlan = ( state, plan ) =>
    state.plans[plan.uid];

export const getStatus = ( state, plan ) =>
    state.statuses[plan.uid];


export const isSamePlan = ( p1, p2 ) => (
    p1.uid === p2.uid
    && p1.id === p2.id
    && p1.updateCount === p2.updateCount
    && p1.name === p2.name
    && p1.blocklyXml === p2.blocklyXml
    && JSON.stringify( p1.documentPlan ) === JSON.stringify( p2.documentPlan )
);

