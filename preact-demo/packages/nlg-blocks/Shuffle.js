import Block                from './Block';
import * as T               from './types';
import valueList            from './value-list';


export default Block({

    ...valueList,

    type:                   'Shuffle',

    json: {
        ...valueList.json,

        colour:             202,
        output:             T.LIST,
        message0:           'in random order:',
    },

    valueListCheck:         T.ANY,
});
