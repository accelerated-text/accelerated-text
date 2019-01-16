import Block                from './Block';


export default Block({

    type:                   'number-comparison',

    json: {
        colour:             202,
        output:             null,
        inputsInline:       true,
        message0:           '%2 %1 %3',
        args0: [{
            type:           'field_dropdown',
            name:           'operator',
            options: [
                [ '=', '==' ],
                [ '≠', '!=' ],
                [ '<', '<' ],
                [ '<=', '<=' ],
                [ '>', '>' ],
                [ '>=', '>=' ],
            ],
        }, {
            type:           'input_value',
            name:           'value1',
        }, {
            type:           'input_value',
            name:           'value2',
        }],
    },
});
