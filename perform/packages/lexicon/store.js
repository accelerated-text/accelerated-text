export default {

    getInitialState: () => ({
        newItem:            null,
        newItemSaved:       false,
        query:              null,
    }),

    lexicon: {

        onChangeQuery: ( query, { state }) => {

            const shouldHideNew = (
                state.newItem
                && state.newItemSaved
                && query !== state.query
            );

            return {
                newItem:        shouldHideNew ? null : state.newItem,
                newItemSaved:   shouldHideNew ? false : state.newItemSaved,
                query,
            };
        },

        onClickNew: ( _, { state }) => (
            ( state.newItem && !state.newItemSaved )
                ? null
                : {
                    newItem: {
                        key:        '',
                        synonyms:   [],
                    },
                    newItemSaved:   false,
                }
        ),

        onSaveNew: newItem => ({
            newItem,
            newItemSaved:       true,
        }),

        onCancelNew: () => ({
            newItem:            null,
            newItemSaved:       false,
        }),
    },
};
