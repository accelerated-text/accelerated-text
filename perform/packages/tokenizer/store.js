export default {

    getInitialState: () => ({
        error:          false,
        loading:        false,
        result:         null,
    }),

    tokenizer: {

        onCallStart: () => ({
            loading:    true,
        }),

        onCallError: error => ({
            error,
            loading:    false,
        }),

        onCallResult: result => ({
            error:      false,
            loading:    false,
            result,
        }),
    },
};
