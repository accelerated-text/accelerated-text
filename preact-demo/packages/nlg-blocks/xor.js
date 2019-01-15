import Block                from './Block';


export default Block({

    type:                   'xor',

    json: {
        colour:             164,
        output:             null,
        message0:           'either %1 or %2',
        args0: [{
            type:           'input_value',
            name:           'value1',
        }, {
            type:           'input_value',
            name:           'value2',
        }],
    },
});
