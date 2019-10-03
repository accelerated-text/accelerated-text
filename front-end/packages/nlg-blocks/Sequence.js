import Block                from './Block';
import { blue as color }    from './colors.sass';
import * as T               from './types';
import TwoInputs            from './icons/TwoInputs';
import valueSequence        from './value-sequence';


export default Block({

    ...valueSequence,

    type:                   'Sequence',
    color,
    icon:                   TwoInputs({ color }),

    json: {
        ...valueSequence.json,

        colour:             color,
        output:             T.LIST,
        message0:           'sequence:',
    },

    valueListCheck:         T.ANY,
});
