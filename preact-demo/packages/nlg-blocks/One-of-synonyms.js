import Block                from './Block';
import * as T               from './types';
import valueList            from './value-list';


export default Block({

    ...valueList,

    type:                   'One-of-synonyms',

    json: {
        ...valueList.json,

        colour:             202,
        output:             T.LIST,
        message0:           'A synonym from:',
    },

    valueListCheck:         T.ANY,
});
