import Block                from './Block';
import { gray as color }    from './colors.sass';
import FieldValueIcon       from './icons/FieldValue';
import * as T               from './types';


export default Block({

    type:                   'Quote',
    color,
    icon:                   FieldValueIcon({ color }),

    json: {
        colour:             color,
        output:             T.STRING,
        message0:           '%1',
        args0: [{
            type:           'field_input',
            name:           'text',
            text:           'some text',
        }],
    },
});
