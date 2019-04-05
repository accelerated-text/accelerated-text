import { BLUE }             from '../styles/blockly-colors';

import Block                from './Block';
import * as T               from './types';
import valueList            from './value-list';


export default Block({

    ...valueList,

    type:                   'One-of-synonyms',

    json: {
        ...valueList.json,

        colour:             BLUE,
        output:             T.LIST,
        message0:           'A synonym from:',
    },

    valueListCheck:         T.ANY,
});
