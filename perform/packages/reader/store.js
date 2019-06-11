import resolvers from       '../graphql/resolvers';

export default {

    getInitialState: () => ({
        flags:              resolvers.Query.readerFlags(),
        flagValues: {
            junior:         true,
            senior:         false,
        },
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
