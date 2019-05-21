export default {

    getInitialState: ({ editing = false, item }) => ({
        editing,
        error:      null,
        item,
        saving:     false,
    }),

    componentWillReceiveProps: ( nextProps, { props, state }) => {
        const shouldUpdate = (
            nextProps.item !== props.item
             && state.item === props.item
        );
        if( shouldUpdate ) {
            return { item: nextProps.item };
        }
    },

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
