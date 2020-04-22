import Block                from './Block';
import { green as color }   from './colors.sass';
import StatementIcon        from './icons/Statement';
import * as T               from './types';
import valueSequence        from './value-sequence';


export default Block({
    ...valueSequence,

    type:                   'Segment',
    color,
    icon:                   StatementIcon({ color }),

    json: {
        ...valueSequence.json,

        colour:             color,
        nextStatement:      T.STRING,
        previousStatement:  [ T.DEFINITION, T.STRING ],
        message0:           'Text segment',
    },

    valueListCheck:         T.ANY,
});
