export default {

    getInitialState: ({ element: { annotations, references }}) => ({
        activeAnnotation:   null,
        activeReference:    null,
        activeWord:         null,
        annotations,
        references,
    }),

    onClickAnnotation: activeAnnotation => ({
        activeAnnotation,
    }),

    onClickReference: activeReference => ({
        activeReference,
    }),

    onClickWord: ( activeWord, { state }) => ({
        activeAnnotation:   null,
        activeReference:    null,
        activeWord: (
            activeWord === state.activeWord
                ? null
                : activeWord
        ),
    }),
};
