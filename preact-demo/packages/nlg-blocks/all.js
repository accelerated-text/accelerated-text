import Block                from './Block';
import * as T               from './types';
import valueList            from './value-list';


export default Block({

    ...valueList,

    type:                   'all',

    json: {
        ...valueList.json,

        colour:             202,
        output:             T.LIST,
        message0:           'all:',
    },

    valueListCheck:         T.ANY,
});
