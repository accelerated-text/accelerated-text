import Block                from './Block';
import * as T               from './types';
import valueSequence        from './value-sequence';


export default Block({
    ...valueSequence,

    type:                   'Product',

    value_count:            1,

    json: {
        ...valueSequence.json,

        colour:             164,
        output:             T.STRING,
        message0:           'Product',
        message1:           'named: %1',
        args1: [{
            type:           'input_value',
            name:           'name',
            check:          T.STRING,
        }],
        message2:           'with %1',
        args2: [{
            type:           'input_value',
            name:           'value_0',
            check:          T.TEXT,
        }],
    },

    valueListCheck:         T.TEXT,
});
