import Block            from './Block';

export default Block({

    type:                   'document-plan',

    json: {
        colour:             '#555555',
        message0:           'Document plan:',
        message1:           '%1',
        args1: [{
            type:           'input_statement',
            name:           'segments',
        }],
    },
});
