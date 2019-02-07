import { BLUE }             from '../styles/blockly-colors';

import Block                from './Block';
import * as T               from './types';


export default Block({

    type:                   'Value-in',

    json: {
        colour:             BLUE,
        output:             T.BOOLEAN,
        inputsInline:       true,
        message0:           '%2 %1 %3',
        args0: [{
            type:           'field_dropdown',
            name:           'operator',
            options: [
                [ 'is in', 'in' ],
            ],
        }, {
            type:           'input_value',
            name:           'value',
            check:          T.STRING,
        }, {
            type:           'input_value',
            name:           'list',
            check:          T.TEXT,
        }],
    },
});
