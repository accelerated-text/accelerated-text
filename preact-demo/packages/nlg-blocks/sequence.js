import Block                from './Block';
import valueSequence        from './value-sequence';


export default Block({

    ...valueSequence,

    type:                   'sequence',
    output:                 null,

    json: {
        ...valueSequence.json,

        colour:             202,
        message0:           'sequence:',
    },
});
