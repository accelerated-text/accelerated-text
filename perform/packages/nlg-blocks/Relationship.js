import Block                from './Block';
import { red as color }     from './colors.sass';
import * as T               from './types';
import TwoInputs            from './icons/TwoInputs';
import valueSequence        from './value-sequence';


export default Block({
    ...valueSequence,

    type:                   'Relationship',
    color,
    icon:                   TwoInputs({ color }),

    json: {
        ...valueSequence.json,

        colour:             color,
        output:             T.STRING,
        message0:           '%1',
        args0: [{
            type:           'field_dropdown',
            name:           'relationshipType',
            options: [
                [ 'Provides', 'provides' ],
                [ 'Consequence', 'consequence' ],
            ],
        }],
    },

    valueListCheck:         T.TEXT,
});
