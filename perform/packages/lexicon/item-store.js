export default {

    getInitialState: ({ editing = false, item }) => ({
        editing,
        error:      null,
        item,
        saving:     false,
    }),

    componentWillReceiveProps: ( nextProps, { props }) => (
        ( nextProps.item !== props.item )
            && { item: nextProps.item }
    ),

    lexiconItem: {

        onClickEdit: ( _, { state }) => ({
            editing:    true,
        }),

        onCancelEdit: () => ({
            editing:    false,
            error:      false,
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
