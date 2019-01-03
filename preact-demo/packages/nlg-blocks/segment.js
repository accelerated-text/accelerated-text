import Block                from './Block';


export default Block({

    type:                   'segment',

    json: {
        colour:             105,
        message0:           '%1',
        args0: [{
            type:           'field_dropdown',
            name:           'text_type',
            options: [
                [ 'Description',    'description' ],
                [ 'Pitch',          'pitch' ],
            ],
        }],
        message1:           'segment',
        message2:           'with %1',
        args2: [{
            type:           'input_value',
            name:           'items',
        }],
    },
});
