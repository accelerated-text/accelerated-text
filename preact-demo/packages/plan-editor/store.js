export default {

    getInitialState: () => ({
        contextName:        null,
        dataSample:         null,
    }),

    planEditor: {
        onChangeContext: ({ contextName }) => ({
            contextName,
        }),

        onClickUpload: ({ dataSample }) => ({
            dataSample,
        }),
    },
};
