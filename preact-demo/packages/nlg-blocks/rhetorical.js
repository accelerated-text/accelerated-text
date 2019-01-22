import Block                from './Block';
import valueSequence        from './value-sequence';


export default Block({
    ...valueSequence,

    type:                   'rhetorical',

    json: {
        ...valueSequence.json,

        colour:             327,
        output:             null,

        message0:           '%1',
        args0: [{
            type:           'field_dropdown',
            name:           'type',
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
        message1:           'from %1',
        args1: [{
            type:           'input_value',
            name:           'value',
        }],
    },
});
