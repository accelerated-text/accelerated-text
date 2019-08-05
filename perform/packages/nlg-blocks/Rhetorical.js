import Block                from './Block';
import { red as color }     from './colors.sass';
import * as T               from './types';
import TwoInputs            from './icons/TwoInputs';
import valueSequence        from './value-sequence';


export default Block({
    ...valueSequence,

    type:                   'Rhetorical',
    color,
    icon:                   TwoInputs({ color }),

    json: {
        ...valueSequence.json,

        colour:             color,
        output:             T.STRING,

        message0:           '%1 from:',
        args0: [{
            type:           'field_dropdown',
            name:           'rstType',
            options: [
                [ 'Attribution', 'attribution' ],
                [ 'Background', 'background' ],
                [ 'Cause', 'cause' ],
                [ 'Comparison', 'comparison' ],
                [ 'Condition', 'condition' ],
                [ 'Contrast', 'contrast' ],
                [ 'Elaboration', 'elaboration' ],
                [ 'Enablement', 'enablement' ],
                [ 'Evaluation', 'evaluation' ],
                [ 'Explanation', 'explanation' ],
                [ 'Joint', 'joint' ],
                [ 'Manner-Means', 'manner-means' ],
                [ 'Topic-Comment', 'topic-comment' ],
                [ 'Summary', 'summary' ],
                [ 'Temporal', 'temporal' ],
                [ 'Topic Change', 'topic-change' ],
            ],
        }],
    },

    valueListCheck:         T.TEXT,
});
