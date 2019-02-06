import { RED }              from '../styles/blockly-colors';

import Block                from './Block';
import * as T               from './types';
import valueSequence        from './value-sequence';


export default Block({
    ...valueSequence,

    type:                   'Rhetorical',

    json: {
        ...valueSequence.json,

        colour:             RED,
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
