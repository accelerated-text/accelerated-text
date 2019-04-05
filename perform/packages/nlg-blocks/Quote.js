import { GRAY }         from '../styles/blockly-colors';

import Block            from './Block';
import * as T           from './types';


export default Block({

    type:                   'Quote',

    json: {
        colour:             GRAY,
        output:             T.STRING,
        message0:           '%1',
        args0: [{
            type:           'field_input',
            name:           'text',
            text:           'some text',
        }],
    },
});
