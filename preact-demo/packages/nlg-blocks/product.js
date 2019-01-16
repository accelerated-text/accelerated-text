import Block                from './Block';
import valueListValue       from './value-list-value';


export default Block({
    ...valueListValue,

    type:                   'product',

    value_count:            1,

    json: {
        ...valueListValue.json,

        colour:             164,
        output:             'Topic',
        message0:           'Product',
        message1:           'named: %1',
        args1: [{
            type:           'input_value',
            name:           'name',
        }],
        message2:           'with %1',
        args2: [{
            type:           'input_value',
            name:           'value_0',
        }],
    },
});
