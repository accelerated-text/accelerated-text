export default {

    getInitialState: () => ({
        dataSampleId:       null,
    }),

    planEditor: {

        onChangeDataSample: dataSampleId => ({
            dataSampleId,
        }),
    },
};
