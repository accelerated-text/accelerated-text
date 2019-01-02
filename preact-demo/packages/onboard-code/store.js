export default {

    getInitialState: () => ({
        textExample:    '',
    }),

    onboardCode: {

        onChangeTextExample: textExample => ({
            textExample,
        }),
    },
};
