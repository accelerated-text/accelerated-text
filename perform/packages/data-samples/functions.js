import itemsWithStatus  from '../store-utils/items-with-status';


const storeFns =            itemsWithStatus( 'key', 'fileItems', 'statuses' );
export const getItem =      storeFns.getItem;
export const getStatus =    storeFns.getStatus;
export const patchItem =    storeFns.patchItem;
export const patchStatus =  storeFns.patchStatus;

export const findFileById = ( dataSamples, id ) => dataSamples.fileItems[id];

export const addItemFields = item => ({
    ...item,
    contentType:    item.contentType || 'text/csv',
    fileName:       item.key.split( '/' ).pop(),
    id:             item.key,
});

export const findFileByPlan  = ( dataSamples, plan ) => (
    plan && plan.dataSampleId
    && findFileById( dataSamples, plan.dataSampleId )
);

export const statusTemplate = {
    getDataError:   null,
    getDataLoading: false,
};

export const getDownloadUrl = ( user, fileItem ) =>
    `${ process.env.DATA_FILES_BUCKET }/${ user.id }/${ fileItem.fileName }`;
