export default {
    
    getInitialState: () => ({
        items:              [],
        requestOffset:      0,
        query:              null,
        resultsError:       null,
        resultsLoading:     false,
        totalCount:         0,

        newItem:            null,
        newItemSaved:       false,
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
                requestOffset:  0,
                query,
            };
        },

        onClickMore: ( _, { state }) => ({
            requestOffset:  state.items.length,
        }),

        onGet: () => ({
            resultsLoading: true,
        }),

        onGetError: resultsError => ({
            resultsError,
            resultsLoading: false,
        }),

        onGetResult: ({ items, offset, query, totalCount }, { state }) => {
            const isUpdate = (
                query === state.query
                && offset
                && offset === state.items.length
            );
            return ({
                items: (
                    isUpdate
                        ? [ ...state.items, ...items ]
                        : items
                ),
                resultsError:   null,
                resultsLoading: false,
                totalCount,
            });
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
