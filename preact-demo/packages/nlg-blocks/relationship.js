import Block                from './Block';
import valueSequence        from './value-sequence';


export default Block({
    ...valueSequence,

    type:                   'relationship',

    json: {
        ...valueSequence.json,

        colour:             56,
        output:             null,
        message0:           '%1',
        args0: [{
            type:           'field_dropdown',
            name:           'type',
            options: [
                [ 'Provides', 'provides' ],
                [ 'Consequence', 'consequence' ],
            ],
        }],
    },
});
