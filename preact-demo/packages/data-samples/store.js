export default {

    getInitialState: () => ({
        files:          null,
        getListError:   null,
        getListLoading: false,
    }),

    dataSamples: {

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

        onGetListResult: files => ({
            files,
            getListError:   null,
            getListLoading: false,
        }),
    },
};
