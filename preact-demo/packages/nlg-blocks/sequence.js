import Block                from './Block';
import valueListValue       from './value-list-value';


export default Block({

    ...valueListValue,

    type:                   'sequence',

    json: {
        ...valueListValue.json,
        colour:             202,
        message0:           'sequence:',
    },

    mutationToDom() {

        const el = valueListValue.mutationToDom.call( this );
        el.setAttribute( 'next_values', 'value_' );
        return el;
    },
});
