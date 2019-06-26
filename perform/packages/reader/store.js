export default {

    getInitialState: () => ({
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
