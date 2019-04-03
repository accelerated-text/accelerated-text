export default {

    getInitialState: ({ editing = false, item }) => ({
        editing,
        error:      null,
        item,
        saving:     false,
    }),

    lexiconItem: {

        onClickEdit: ( _, { state }) => ({
            editing:    true,
        }),

        onCancelEdit: () => ({
            editing:    false,
        }),

        onSave: ( synonyms, { state }) => ({
            editing:    false,
            item: {
                ...state.item,
                synonyms,
            },
            saving:     true,
        }),

        onSaveError: error => ({
            error,
            saving:     false,
        }),

        onSaveSuccess: item => ({
            error:      null,
            item,
            saving:     false,
        }),
    },
};
