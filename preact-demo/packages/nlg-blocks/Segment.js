import Block                from './Block';
import * as T               from './types';
import valueSequence        from './value-sequence';


export default Block({
    ...valueSequence,

    type:                   'Segment',

    json: {
        ...valueSequence.json,

        colour:             105,
        nextStatement:      T.STRING,
        previousStatement:  [ T.DEFINITION, T.STRING ],
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

    valueListCheck:         T.TEXT,
});
