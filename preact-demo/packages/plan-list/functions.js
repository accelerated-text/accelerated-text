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

export const findById = ( list, id ) =>
    id
        ? list.find( item => item.id === id )
        : null;

export const findIndexById = ( list, id ) =>
    id
        ? list.findIndex( item => item.id === id )
        : -1;

export const findIndexByItem = ( list, item ) =>
    ( item && item.id )
        ? list.findIndex( listItem => listItem.id === item.id )
        : -1;

export const removeItem = ( list, item ) => {

    const idx =     findIndexByItem( list, item );
    if( idx === -1 ) {
        return list;
    } else {
        const newList =    [ ...list ];
        newList.splice( idx, 1 );
        return newList;
    }
};

export const updateItem = ( list, newItem ) => {

    const idx =     findIndexByItem( list, newItem );
    if( idx === -1 ) {
        return list;
    } else {
        const newList =    [ ...list ];
        newList.splice( idx, 1, newItem );
        return newList;
    }
};

export const getActiveId = ( list, currentId ) =>
    findById( list, currentId )
        ? currentId
        : list[0] && list[0].id || null;
