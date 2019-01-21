import Block                from './Block';
import * as T               from './types';
import valueSequence        from './value-sequence';


export default Block({
    ...valueSequence,

    type:                   'relationship',

    json: {
        ...valueSequence.json,

        colour:             56,
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
