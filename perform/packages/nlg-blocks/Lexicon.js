import { BLUER }        from '../styles/blockly-colors';


import Block            from './Block';
import * as T           from './types';


export default Block({

    type:                   'Lexicon',

    json: {
        colour:             BLUER,
        output:             T.LIST,
        message0:           'Word list %1',
        args0: [{
            type:           'field_input',
            name:           'text',
            text:           'good',
        }],
    },
});
