import Block                from './Block';
import * as T               from './types';


export default Block({

    type:                   'Define-var',

    json: {

        colour:             327,
        nextStatement:      [ T.DEFINITION, T.STRING ],
        previousStatement:  T.DEFINITION,

        message0:           'set %1 to',
        args0: [{
            type:           'field_variable',
            name:           'name',
            variable:       '%{BKY_VARIABLES_DEFAULT_NAME}',
        }],
        message1:           '%1',
        args1: [{
            type:           'input_value',
            name:           'value',
            check:          T.TEXT,
        }],
    },
});
