import Block                from './Block';
import * as T               from './types';
import valueSequence        from './value-sequence';


export default Block({

    ...valueSequence,

    type:                   'Sequence',

    json: {
        ...valueSequence.json,

        colour:             202,
        output:             T.LIST,
        message0:           'sequence:',
    },

    valueListCheck:         T.ANY,
});
