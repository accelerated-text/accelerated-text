import Block                from './Block';
import { cyan as color }    from './colors.sass';
import ModifierIcon         from './icons/Modifier';
import * as T               from './types';


export default Block({

    type:                   'Not',
    color,
    icon:                   ModifierIcon({ color }),

    json: {
        colour:             color,
        output:             T.BOOLEAN,
        message0:           'not %1',
        args0: [{
            type:           'input_value',
            name:           'value',
            check:          T.ATOMIC_VALUE,
        }],
    },
});
