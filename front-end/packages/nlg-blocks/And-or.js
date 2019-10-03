import Block                from './Block';
import { cyan as color }    from './colors.sass';
import * as T               from './types';
import TwoInputs            from './icons/TwoInputs';
import valueSequence        from './value-sequence';


export default Block({

    ...valueSequence,

    type:                   'And-or',
    color,
    icon:                   TwoInputs({ color }),

    json: {
        ...valueSequence.json,

        colour:             color,
        output:             T.BOOLEAN,
        message0:           '%1',
        args0: [{
            type:           'field_dropdown',
            name:           'operator',
            options: [
                [ 'and',        'and' ],
                [ 'or',         'or' ],
            ],
        }],
    },

    valueListCheck:         T.ATOMIC_VALUE,
});
