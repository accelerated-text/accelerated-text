export default {

    getInitialState: () => ({
        error:          false,
        loading:        false,
        pending:        false,
        result:         null,
    }),

    variantsApi: {

        onGet: ( _, { state }) => (
            state.loading
                ? { pending: true }
                : null
        ),

        onGetStart: () => ({
            loading:    true,
            pending:    false,
        }),

        onGetAbort: () => ({
            loading:    false,
        }),

        onGetError: error => ({
            error,
            loading:    false,
        }),

        onGetResult: result => ({
            error:      false,
            loading:    false,
            result,
        }),
    },
};
