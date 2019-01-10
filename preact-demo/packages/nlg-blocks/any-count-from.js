import Block                from './Block';
import valueListValue       from './value-list-value';


export default Block({

    ...valueListValue,

    type:                   'any-count-from',

    json: {
        ...valueListValue.json,
        colour:             202,
        message0:           'any %1',
        args0: [{
            type:           'field_number',
            name:           'count',
            value:          1,
        }],
        message1:           'from:',
    },
});
