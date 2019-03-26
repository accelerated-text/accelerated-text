export default {

    getInitialState: ({ editing = false, item }) => ({
        editText:   null,
        editing,
        error:      null,
        item,
        saving:     false,
    }),

    lexiconItem: {

        onChangeText: editText => ({
            editText,
        }),

        onClickEdit: ( _, { state }) => ({
            editing:    true,
            editText:   state.item.synonyms.join( '\n' ),
        }),

        onCancelEdit: () => ({
            editing:    false,
        }),

        onSave: ( _, { state }) => ({
            editing:    false,
            item: {
                ...state.item,
                synonyms:   state.editText.trim().split( '\n' ),
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
