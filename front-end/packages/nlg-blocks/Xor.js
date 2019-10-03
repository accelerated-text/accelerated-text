import Block                from './Block';
import { cyan as color }    from './colors.sass';
import * as T               from './types';
import TwoInputs            from './icons/TwoInputs';


export default Block({

    type:                   'Xor',
    color,
    icon:                   TwoInputs({ color }),

    json: {
        colour:             color,
        inputsInline:       false,
        output:             T.BOOLEAN,
        message0:           'either: %1 or: %2 ...but not both',
        args0: [{
            type:           'input_value',
            name:           'value1',
            check:          T.ATOMIC_VALUE,
        }, {
            type:           'input_value',
            name:           'value2',
            check:          T.ATOMIC_VALUE,
        }],
    },
});
