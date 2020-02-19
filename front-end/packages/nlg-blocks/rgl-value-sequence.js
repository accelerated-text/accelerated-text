import Block                from './Block';
import valueList            from './rgl-value-list';


export default Block({

    ...valueList,

    mutationToDom() {

        const el = valueList.mutationToDom.call( this );
        el.setAttribute( 'value_sequence', 'value_' );
        return el;
    },
});
