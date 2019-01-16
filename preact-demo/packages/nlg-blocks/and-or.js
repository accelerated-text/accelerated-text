import Block                from './Block';
import valueSequence        from './value-sequence';


export default Block({

    ...valueSequence,

    type:                   'and-or',

    json: {
        ...valueSequence.json,

        colour:             164,
        output:             null,
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
});
