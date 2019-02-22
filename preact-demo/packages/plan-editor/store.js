export default {

    getInitialState: () => ({
        dataSample:         'shoes.csv',
    }),

    planEditor: {
        onClickUpload: ({ dataSample }) => ({
            dataSample,
        }),
    },
};
