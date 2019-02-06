import Block                from './Block';
import * as T               from './types';
import valueSequence        from './value-sequence';


export default Block({
    ...valueSequence,

    type:                   'Product',

    json: {
        ...valueSequence.json,

        colour:             164,
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
