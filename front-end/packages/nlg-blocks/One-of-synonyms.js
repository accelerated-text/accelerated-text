import Block                from './Block';
import { blue as color }    from './colors.sass';
import * as T               from './types';
import TwoInputs            from './icons/TwoInputs';
import valueList            from './value-list';


export default Block({

    ...valueList,

    type:                   'One-of-synonyms',
    color,
    icon:                   TwoInputs({ color }),

    json: {
        ...valueList.json,

        colour:             color,
        output:             T.LIST,
        message0:           'A synonym from:',
    },

    valueListCheck:         T.ANY,
});
