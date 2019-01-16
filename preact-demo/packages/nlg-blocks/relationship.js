import Block                from './Block';
import valueListValue       from './value-list-value';


export default Block({
    ...valueListValue,

    type:                   'relationship',

    json: {
        ...valueListValue.json,

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
