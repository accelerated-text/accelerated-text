import { BLUER }        from '../styles/blockly-colors';


import Block            from './Block';
import * as T           from './types';


export default Block({

    type:                   'Lexicon',

    json: {
        colour:             BLUER,
        output:             T.LIST,
        message0:           'List: %1',
        args0: [{
            type:           'field_label',
            name:           'text',
            text:           'good',
        }],
    },
});
