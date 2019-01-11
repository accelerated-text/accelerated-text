import Block                from './Block';
import valueListValue       from './value-list-value';


export default Block({

    ...valueListValue,

    type:                   'and-or',

    json: {
        ...valueListValue.json,
        colour:             164,
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
