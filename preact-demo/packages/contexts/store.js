export default {

    getInitialState: () => ({
        contexts:       null,
        getListError:   null,
        getListLoading: false,
    }),

    contexts: {

        onGetList: ( _, { state }) => (
            state.getListLoading && {
                getListError:   'Will not start a new request while the previous one is not finished. Please wait.',
            }
        ),

        onGetListStart: () => ({
            getListLoading: true,
        }),

        onGetListError: getListError => ({
            getListError,
            getListLoading: false,
        }),

        onGetListResult: contexts => ({
            contexts,
            getListError:   null,
            getListLoading: false,
        }),
    },
};
