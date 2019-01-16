import Block            from './Block';

export default Block({

    type:                   'quote',

    json: {
        colour:             26,
        output:             null,
        message0:           '%1',
        args0: [{
            type:           'field_input',
            name:           'text',
            text:           'some text',
        }],
    },
});
