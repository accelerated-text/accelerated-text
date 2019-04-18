import itemsWithStatus  from '../store-utils/items-with-status';


const storeFns =            itemsWithStatus( 'key', 'fileItems', 'statuses' );
export const getFile =      storeFns.getItem;
export const getStatus =    storeFns.getStatus;
export const patchFile =    storeFns.patchItem;
export const patchStatus =  storeFns.patchStatus;

export const findFileById = ( dataSamples, id ) => (
    dataSamples.files
    && dataSamples.files.find( file => file.id === id )
);

export const addItemFields = item => ({
    ...item,
    contentType:    item.contentType || 'text/csv',
    id:             item.key,
    fileName:       item.key.split( '/' ).pop(),
});

export const findFileByPlan  = ( dataSamples, plan ) => (
    plan && plan.dataSampleId
    && findFileById( dataSamples, plan.dataSampleId )
);
