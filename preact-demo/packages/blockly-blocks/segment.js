import Block                from './Block';


export default Block({

    type:                   'segment',

    json: {
        colour:             32,
        nextStatement:      'Action',
        previousStatement:  'Action',
        message0:           '%1 segment',
        args0: [{
            type:           'field_dropdown',
            name:           'GOAL',
            options: [
                [ 'Description',    'description' ],
                [ 'Pitch',          'ditch' ],
            ],
        }],
        message1:           'with %1',
        args1: [{
            type:           'input_statement',
            name:           'CHILDREN',
        }],
    },
});
