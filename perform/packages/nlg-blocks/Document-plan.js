import Block                from './Block';
import { black as color }   from './colors.sass';
import DocumentPlanIcon     from './icons/DocumentPlan';
import * as T               from './types';


export default Block({

    type:                   'Document-plan',
    color,
    icon:                   DocumentPlanIcon({ color }),

    json: {
        colour:             color,
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
