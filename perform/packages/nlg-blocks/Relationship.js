import { RED }              from '../styles/blockly-colors';

import Block                from './Block';
import * as T               from './types';
import valueSequence        from './value-sequence';


export default Block({
    ...valueSequence,

    type:                   'Relationship',

    json: {
        ...valueSequence.json,

        colour:             RED,
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
