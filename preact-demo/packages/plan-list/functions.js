import uuid             from 'uuid';

export const sortByCreatedAt = ( a, b ) => (
    ( a.createdAt && b.createdAt )
        ? ( b.createdAt - a.createdAt )
    : a.createdAt
        ? -1
    : b.createdAt
        ? 1
    : -1
);

export const sortPlans = list =>
    list.sort( sortByCreatedAt );

export const addMissingUids = list =>
    list.map( item => ({
        ...item,
        uid:    item.uid || uuid.v4(),
    }));


export const findByUid = ( list, uid ) =>
    uid
        ? list.find( item => item.uid === uid )
        : null;

export const findIndexByUid = ( list, uid ) =>
    uid
        ? list.findIndex( item => item.uid === uid )
        : -1;

export const findIndexByItem = ( list, item ) =>
    findIndexByUid( list, item && item.uid );


export const removeItem = ( list, item ) => {

    const idx =         findIndexByItem( list, item );
    if( idx === -1 ) {
        return list;
    } else {
        const newList = [ ...list ];
        newList.splice( idx, 1 );
        return newList;
    }
};

export const updateItem = ( list, newItem ) => {

    const idx =         findIndexByItem( list, newItem );
    if( idx === -1 ) {
        throw Error( `Item ${ newItem.uid } not found in document plan list.` );
    }

    const newList =     [ ...list ];
    newList.splice( idx, 1, newItem );
    return newList;
};


export const getActiveUid = ( list, currentId ) =>
    findByUid( list, currentId )
        ? currentId
        : list[0] && list[0].uid || null;


export const patchStatus = ( state, uid, patch ) => ({
    statuses: {
        ...state.statuses,
        [uid]: {
            ...state.statuses[uid],
            ...patch,
        },
    },
});
