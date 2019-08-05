import Block                from './Block';
import ModifierIcon         from './icons/Modifier';
import { red as color }     from './colors.sass';
import * as T               from './types';


export default Block({

    type:                   'Modifier',
    color,
    icon:                   ModifierIcon({ color }),

    json: {
        colour:             color,
        output:             T.LIST,
        message0:           'a.%1',
        args0: [{
            type:           'input_value',
            name:           'lexicon',
            check:          T.TEXT,
        }],
        message1:           'n.%1',
        args1: [{
            type:           'input_value',
            name:           'input',
            check:          T.TEXT,
        }],
    },
});
