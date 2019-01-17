import Block                from './Block';
import * as T               from './types';
import valueList            from './value-list';


export default Block({

    ...valueList,

    type:                   'any-count-from',

    json: {
        ...valueList.json,

        colour:             202,
        output:             T.LIST,
        message0:           'any %1',
        args0: [{
            type:           'field_number',
            name:           'count',
            value:          1,
        }],
        message1:           'from:',
    },

    valueListCheck:         T.ANY,
});
