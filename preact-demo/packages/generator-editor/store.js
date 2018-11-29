export default {

    getInitialState: () => ({
        blocks:         [],
        contextName:    null,
        dataSample:     null,
        generatorName:  'Example Generator',
    }),

    onChangeContext:    ({ contextName }) => ({ contextName }),

    onClickUpload:      ({ dataSample }) => ({ dataSample }),
};
