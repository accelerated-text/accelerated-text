import { h, Component } from 'preact';

import { useStores }    from '../vesa/';

import SelectContext    from './SelectContext';


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
                ( <SelectContext onChange={ this.onChange } value={ plan.contextId } /> )
            </span>
        );
    }
});
