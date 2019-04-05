import { BLUE }             from '../styles/blockly-colors';

import Block                from './Block';
import * as T               from './types';
import valueSequence        from './value-sequence';


export default Block({

    ...valueSequence,

    type:                   'Sequence',

    json: {
        ...valueSequence.json,

        colour:             BLUE,
        output:             T.LIST,
        message0:           'sequence:',
    },

    valueListCheck:         T.ANY,
});
