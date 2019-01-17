import Block                from './Block';
import * as T               from './types';


export default Block({

    type:                   'xor',

    json: {
        colour:             164,
        output:             T.BOOLEAN,
        message0:           'either %1 or %2',
        args0: [{
            type:           'input_value',
            name:           'value1',
            check:          T.ATOMIC_VALUE,
        }, {
            type:           'input_value',
            name:           'value2',
            check:          T.ATOMIC_VALUE,
        }],
    },
});
