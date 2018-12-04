import tokenizer from '../tokenizer/tokenizer';


const EXAMPLE_BLOCKS = [{
    type:       'SEGMENT',
    goal:       'description',
    children: [{
        type:   'ATTRIBUTE',
        text:   'color',
    }, {
        type:   'ATTRIBUTE',
        text:   'material',
    }, {
        type:   'ATTRIBUTE',
        text:   'make',
    }],
}];


export default {

    getInitialState: () => ({
        blocks:             null,
        contextName:        null,
        dataSample:         null,
        generatorName:      'Example Generator',
        tokenizerLoading:   false,
        tokenizerError:     null,
    }),

    onChangeContext:    ({ contextName }) => ({ contextName }),

    onClickUpload:      ({ dataSample }) => ({ dataSample }),

    onClickAddOnboardSegment:   () => ({ blocks: EXAMPLE_BLOCKS }),

    onSubmitTextExample: ({ text }, E ) => {

        /// Prevent new requests while the previous one is not finished:
        if( E.state.tokenizerLoading ) {
            return;
        }

        E.setState({
            tokenizerLoading:           true,
        });
        tokenizer( text )
            .then( blocks => E.setState({
                blocks,
                tokenizerError:         false,
                tokenizerLoading:       false,
            }))
            .catch( tokenizerError => E.setState({
                tokenizerError,
                tokenizerLoading:       false,
            }));
    },
};
