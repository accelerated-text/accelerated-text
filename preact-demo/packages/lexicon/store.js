export default {
    
    getInitialState: () => ({
        items:              [],
        requestOffset:      0,
        query:              null,
        resultsError:       null,
        resultsLoading:     false,
        totalCount:         0,
    }),

    lexicon: {

        onChangeQuery: query => ({
            requestOffset:  0,
            query,
        }),

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
    },
};
