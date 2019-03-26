export default {

    getInitialState: props => ({
        editText:   null,
        editing:    false,
        error:      null,
        item:       props.item,
        loading:    false,
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

        onUpdate: ( _, { state }) => ({
            editing:    false,
            item: {
                ...state.item,
                synonyms:   state.editText.trim().split( '\n' ),
            },
            loading:    true,
        }),

        onUpdateError: error => ({
            error,
            loading:    false,
        }),

        onUpdateSuccess: item => ({
            error:      null,
            item,
            loading:    false,
        }),
    },
};
