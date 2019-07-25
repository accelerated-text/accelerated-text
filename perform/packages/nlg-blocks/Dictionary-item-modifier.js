import { MAGENTA }          from '../styles/blockly-colors';

import Block                from './Block';
import DictionaryItem       from './Dictionary-item';
import * as T               from './types';


export default Block({

    ...DictionaryItem,

    type:                   'Dictionary-item-modifier',

    json: {
        ...DictionaryItem.json,
        colour:             MAGENTA,
        message0:           'Dict.: %1, %2',
        args0: [
            ...DictionaryItem.json.args0,
            {
                type:       'input_value',
                name:       'value',
                check:      T.TEXT,
            },
        ],
    },
});
