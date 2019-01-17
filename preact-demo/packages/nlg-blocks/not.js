import Block                from './Block';
import * as T               from './types';


export default Block({

    type:                   'not',

    json: {
        colour:             164,
        output:             T.BOOLEAN,
        message0:           'not %1',
        args0: [{
            type:           'input_value',
            name:           'value',
            check:          T.ATOMIC_VALUE,
        }],
    },
});
