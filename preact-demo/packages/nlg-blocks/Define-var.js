import Block                from './Block';
import * as T               from './types';


export default Block({

    type:                   'Define-var',

    json: {

        colour:             327,
        nextStatement:      [ T.DEFINITION, T.STRING ],
        previousStatement:  [ T.DEFINITION, T.STRING ],

        message0:           '%{BKY_VARIABLES_SET}',
        args0: [{
            type:           'field_variable',
            name:           'name',
            variable:       '%{BKY_VARIABLES_DEFAULT_NAME}',
        }, {
            type:           'input_value',
            name:           'value',
            check:          T.TEXT,
        }],
    },
});
