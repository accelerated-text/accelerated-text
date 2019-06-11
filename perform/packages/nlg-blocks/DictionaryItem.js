import { BLUER }        from '../styles/blockly-colors';


import Block            from './Block';
import * as T           from './types';


export default Block({

    type:                   'DictionaryItem',

    json: {
        colour:             BLUER,
        output:             T.LIST,
        message0:           'Dict.: %1',
        args0: [{
            type:           'field_input',
            name:           'name',
            text:           'see',
        }],
    },
});
