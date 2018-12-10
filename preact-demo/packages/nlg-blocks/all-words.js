import Block                from './Block';
import valueListStatement   from './value-list-statement';


export default Block({

    ...valueListStatement,

    type:                   'all-words',

    json: {
        ...valueListStatement.json,
        colour:             180,
        message0:           'all words',
    },
});
