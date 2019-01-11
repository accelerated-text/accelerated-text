import Block                from './Block';


export default Block({

    type:                   'not',

    json: {
        colour:             164,
        output:             null,
        message0:           'not %1',
        args0: [{
            type:           'input_value',
            name:           'value',
        }],
    },
});
