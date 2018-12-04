import { h, Component } from 'preact';
import shallowEqual     from 'shallow-equal';

export default storeName => Child =>
    class ContextConsumer extends Component {

        shouldComponentUpdate( nextProps, nextState, nextContext ) {
            return (
                !shallowEqual( nextProps, this.props )
                || !shallowEqual( nextContext[storeName], this.context[storeName])
            );
        }

        render( props, state, context ) {
            return h( Child, {
                ...props,
                [storeName]: context && context[storeName] || {},
            });
        }
    };
