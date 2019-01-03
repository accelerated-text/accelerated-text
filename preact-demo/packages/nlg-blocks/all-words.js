import Block                from './Block';
import valueListValue       from './value-list-value';


export default Block({

    ...valueListValue,

    type:                   'all-words',

    json: {
        ...valueListValue.json,
        colour:             202,
        message0:           'all words',
    },
});
