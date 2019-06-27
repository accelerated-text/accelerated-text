export default {

    getInitialState: () => ({
        flagValues:         {},
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
