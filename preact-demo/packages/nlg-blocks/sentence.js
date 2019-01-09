import Block                from './Block';
import valueListValue       from './value-list-value';


export default Block({

    ...valueListValue,

    type:                   'sentence',

    json: {
        ...valueListValue.json,
        colour:             300,
        message0:           'Sentence',
    },
});
