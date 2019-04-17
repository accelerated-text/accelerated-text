import uuid             from 'uuid';

import itemsWithStatus  from '../store-utils/items-with-status';
import planTemplate     from './plan-template';


const storeFns =            itemsWithStatus( 'uid', 'plans', 'statuses' );
export const getPlan =      storeFns.getItem;
export const getStatus =    storeFns.getStatus;
export const patchPlan =    storeFns.patchItem;
export const patchStatus =  storeFns.patchStatus;

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

