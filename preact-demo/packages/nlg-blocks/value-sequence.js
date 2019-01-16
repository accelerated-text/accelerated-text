import Block                from './Block';
import valueList            from './value-list';


export default Block({

    ...valueList,

    mutationToDom() {

        const el = valueList.mutationToDom.call( this );
        el.setAttribute( 'next_values', 'value_' );
        return el;
    },
});
