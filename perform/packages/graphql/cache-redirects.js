export default {
    Query: {
        dictionaryItem: ( _, { id }, { getCacheKey }) =>
            getCacheKey({ __typename: 'DictionaryItem', id }),
    },
};
