import Block                from './Block';
import { blue as color }    from './colors.sass';
import * as T               from './types';
import TwoInputs            from './icons/TwoInputs';
import valueList            from './value-list';


export default Block({

    ...valueList,

    type:                   'Shuffle',
    color,
    icon:                   TwoInputs({ color }),

    json: {
        ...valueList.json,

        colour:             color,
        output:             T.LIST,
        message0:           'in random order:',
    },

    valueListCheck:         T.ANY,
});
