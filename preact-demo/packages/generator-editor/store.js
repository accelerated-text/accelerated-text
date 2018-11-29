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

        E.setState({
            tokenizerLoading:           true,
        });
        tokenizer( text )
            .then( blocks => E.setState({
                blocks,
                tokenizerLoading:       false,
            }))
            .catch( tokenizerError => E.setState({
                tokenizerError,
                tokenizerLoading:       false,
            }));
    },
};
