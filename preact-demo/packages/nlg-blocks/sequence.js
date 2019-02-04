import Block                from './Block';
import * as T               from './types';
import valueSequence        from './value-sequence';


export default Block({

    ...valueSequence,

    type:                   'sequence',
    output:                 T.LIST,

    json: {
        ...valueSequence.json,

        colour:             202,
        message0:           'sequence:',
    },

    valueListCheck:         T.ANY,
});
