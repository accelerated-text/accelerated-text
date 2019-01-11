import Block                from './Block';


export default Block({

    type:                   'value-in-list',

    json: {
        colour:             202,
        output:             null,
        inputsInline:       true,
        message0:           '%2 %1 %3',
        args0: [{
            type:           'field_dropdown',
            name:           'operator',
            options: [
                [ 'is in', 'in' ],
            ],
        }, {
            type:           'input_value',
            name:           'value',
        }, {
            type:           'input_value',
            name:           'list',
        }],
    },
});
