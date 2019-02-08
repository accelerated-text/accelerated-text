export default {

    getInitialState: () => ({
        contextName:        'Shoes',
        dataSample:         'shoes.csv',
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
