import { CYAN }             from '../styles/blockly-colors';

import Block                from './Block';
import * as T               from './types';
import valueSequence        from './value-sequence';


export default Block({

    ...valueSequence,

    type:                   'And-or',

    json: {
        ...valueSequence.json,

        colour:             CYAN,
        output:             T.BOOLEAN,
        message0:           '%1',
        args0: [{
            type:           'field_dropdown',
            name:           'operator',
            options: [
                [ 'and',        'and' ],
                [ 'or',         'or' ],
            ],
        }],
    },

    valueListCheck:         T.ATOMIC_VALUE,
});
