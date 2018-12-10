import Block                from './Block';
import valueListStatement   from './value-list-statement';


export default Block({

    ...valueListStatement,

    type:                   'sentence',

    json: {
        ...valueListStatement.json,
        colour:             300,
        message0:           'Sentence',
    },
});
