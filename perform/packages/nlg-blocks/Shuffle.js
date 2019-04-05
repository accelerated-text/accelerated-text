import { BLUE }             from '../styles/blockly-colors';

import Block                from './Block';
import * as T               from './types';
import valueList            from './value-list';


export default Block({

    ...valueList,

    type:                   'Shuffle',

    json: {
        ...valueList.json,

        colour:             BLUE,
        output:             T.LIST,
        message0:           'in random order:',
    },

    valueListCheck:         T.ANY,
});
