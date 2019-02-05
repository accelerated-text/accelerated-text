import Block            from './Block';
import * as T           from './types';


export default Block({

    type:                   'Document-plan',

    json: {
        colour:             '#555555',
        message0:           'Document plan:',
        message1:           '%1',
        args1: [{
            type:           'input_statement',
            name:           'segments',
            check:          [ T.DEFINITION, T.STRING ],
        }],
    },

    init() {

        this.setDeletable( false );
    },
});
