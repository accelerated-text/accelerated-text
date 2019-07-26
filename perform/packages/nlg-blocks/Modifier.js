import { MAGENTA }          from '../styles/blockly-colors';

import Block                from './Block';
import * as T               from './types';


export default Block({

    type:                   'Modifier',

    json: {
        colour:             MAGENTA,
        output:             T.LIST,
        message0:           '  %1',
        args0: [{
            type:           'input_value',
            name:           'input',
            check:          T.TEXT,
        }],
        message1:           'is%1',
        args1: [{
            type:           'input_value',
            name:           'lexicon',
            check:          T.TEXT,
        }],
    },
});
