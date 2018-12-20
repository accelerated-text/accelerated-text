export default {

    getInitialState: () => ({
        error:          false,
        loading:        false,
        result:         null,
    }),

    variantsApi: {

        onGet: () => ({
            loading:    true,
        }),

        onGetError: error => ({
            error,
            loading:    false,
        }),

        onGetSuccess: result => ({
            error:      false,
            loading:    false,
            result,
        }),
    },
};
