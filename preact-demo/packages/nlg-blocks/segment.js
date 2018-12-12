import Block                from './Block';


export default Block({

    type:                   'segment',

    json: {
        colour:             105,
        nextStatement:      'Action',
        previousStatement:  'Action',
        message0:           '%1 segment',
        args0: [{
            type:           'field_dropdown',
            name:           'goal',
            options: [
                [ 'Description',    'description' ],
                [ 'Pitch',          'pitch' ],
            ],
        }],
        message1:           'with %1',
        args1: [{
            type:           'input_statement',
            name:           'first_child',
        }],
    },
});
