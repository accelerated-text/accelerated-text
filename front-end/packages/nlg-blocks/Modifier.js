import Block                from './Block';
import { red as color }     from './colors.sass';
import * as T               from './types';
import TwoInputs            from './icons/TwoInputs';


export default Block({

    type:                   'Modifier',
    color,
    icon:                   TwoInputs({ color }),

    json: {
        colour:             color,
        output:             T.LIST,
        message0:           'mod. %1',
        args0: [{
            type:           'input_value',
            name:           'modifier',
            check:          T.ANY,
        }],
        message1:           '%1',
        args1: [{
            type:           'input_value',
            name:           'child',
            check:          T.ANY,
        }],
    },
});
