export default {

    getInitialState: () => ({
        error:          false,
        loading:        false,
        result:         null,
    }),

    tokenizer: {

        onCall: () => ({
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
