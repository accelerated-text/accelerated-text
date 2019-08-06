import Block                from './Block';
import { blue as color }    from './colors.sass';
import FieldInputIcon       from './icons/FieldInput';
import * as T               from './types';


export default Block({

    type:                   'Value-comparison',
    color,
    icon:                   FieldInputIcon({ color }),

    json: {
        colour:             color,
        output:             T.BOOLEAN,
        inputsInline:       true,
        message0:           '%2 %1 %3',
        args0: [{
            type:           'field_dropdown',
            name:           'operator',
            options: [
                [ '=', '==' ],
                [ '≠', '!=' ],
                [ '<', '<' ],
                [ '<=', '<=' ],
                [ '>', '>' ],
                [ '>=', '>=' ],
            ],
        }, {
            type:           'input_value',
            name:           'value1',
            check:          T.STRING,
        }, {
            type:           'input_value',
            name:           'value2',
            check:          T.STRING,
        }],
    },
});
