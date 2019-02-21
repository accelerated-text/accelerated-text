import { h, Component } from 'preact';

import ContextsSelect   from '../contexts/Select';
import { useStores }    from '../vesa/';


export default useStores([
    'documentPlans',
])( class Context extends Component {

    onChange = contextId =>
        this.props.E.documentPlans.onUpdate({
            ...this.props.plan,
            contextId,
        });

    render({ className, plan }) {
        return (
            <span className={ className }>
                ( <ContextsSelect onChange={ this.onChange } value={ plan.contextId } /> )
            </span>
        );
    }
});
