import Block                from './Block';
import * as T               from './types';
import TwoInputs            from './icons/TwoInputs';
import valueSequence        from './value-sequence';
import { yellow as color }  from './colors.sass';


export default Block({
    ...valueSequence,

    type:                   'Product',
    color,
    icon:                   TwoInputs({ color }),

    json: {
        ...valueSequence.json,

        colour:             color,
        output:             T.STRING,
        message0:           'Product named: %1',
        args0: [{
            type:           'input_value',
            name:           'name',
            check:          T.STRING,
        }],
    },

    valueListCheck:         T.TEXT,
});
