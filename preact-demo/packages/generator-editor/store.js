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
        blocks:         null,
        contextName:    null,
        dataSample:     null,
        generatorName:  'Example Generator',
    }),

    onChangeContext:    ({ contextName }) => ({ contextName }),

    onClickUpload:      ({ dataSample }) => ({ dataSample }),

    onClickAddOnboardSegment:   () => ({ blocks: EXAMPLE_BLOCKS }),

    onSubmitTextExample: ({ text }, element ) => {

        tokenizer( text )
            .then( blocks => element.setState({ blocks }));
    },
};
