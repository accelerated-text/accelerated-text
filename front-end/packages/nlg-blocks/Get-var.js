import Block                from './Block';
import { magenta as color } from './colors.sass';
import * as T               from './types';
import ValueIcon            from './icons/Value';


export default Block({

    type:                   'Get-var',
    color,
    icon:                   ValueIcon({ color }),

    json: {
        colour:             color,
        output:             T.TEXT,

        message0:           '%1 variable',
        args0: [{
            type:           'field_variable',
            name:           'name',
            variable:       '%{BKY_VARIABLES_DEFAULT_NAME}',
        }],
    },
});
