const patchDictionaryItem = ( obj, dictName, itemKey, patch ) => ({
    [dictName]: {
        ...obj[dictName],
        [itemKey]: {
            ...obj[dictName][itemKey],
            ...patch,
        },
    },
});


export default (
    itemKeyName =       'id',
    itemsDictName =     'items',
    statusesDictName =  'statuses',
) => ({
    getItem: ( obj, item ) =>
        obj[itemsDictName][ item[itemKeyName] ],

    getItemByKey: ( obj, itemKey ) =>
        obj[itemsDictName][itemKey],

    getStatus: ( obj, item ) =>
        obj[statusesDictName][ item[itemKeyName] ],

    getStatusByKey: ( obj, itemKey ) =>
        obj[statusesDictName][itemKey],

    patchItem: ( obj, item ) =>
        patchDictionaryItem( obj, itemsDictName, item[itemKeyName], item ),

    patchStatus: ( obj, item, status ) =>
        patchDictionaryItem( obj, statusesDictName, item[itemKeyName], status ),
});
