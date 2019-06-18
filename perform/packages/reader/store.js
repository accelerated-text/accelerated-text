import resolvers from       '../graphql/resolvers';

export default {

    getInitialState: () => ({
        flagValues: {
            junior:         true,
            senior:         false,
        },
        readerFlags:        resolvers.Query.readerFlags(),
    }),

    reader: {

        onToggleFlag: ( id, { state }) => ({
            flagValues: {
                ...state.flagValues,
                [id]:       !state.flagValues[id],
            },
        }),
    },
};
