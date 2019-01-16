import Block                from './Block';
import valueListStatement   from './value-list-statement';


export default Block({
    ...valueListStatement,

    type:                   'segment',

    json: {
        ...valueListStatement.json,

        colour:             105,
        nextStatement:      'Segment',
        previousStatement:  'Segment',
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
        message2:           'about',
    },
});
