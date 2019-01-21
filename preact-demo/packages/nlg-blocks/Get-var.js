import Block            from './Block';
import * as T           from './types';


export default Block({

    type:                   'Get-var',

    json: {
        colour:             26,
        output:             T.TEXT,

        message0:           '%1 variable',
        args0: [{
            type:           'field_variable',
            name:           'name',
            variable:       '%{BKY_VARIABLES_DEFAULT_NAME}',
        }],
    },
});
