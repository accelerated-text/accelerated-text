import { h, Component } from 'preact';
import shallowEqual     from 'shallow-equal';

export default propName => Child =>
    class ContextConsumer extends Component {

        shouldComponentUpdate( nextProps, nextState, nextContext ) {
            return (
                !shallowEqual( nextProps, this.props )
                || !shallowEqual( nextContext[propName], this.context[propName])
            );
        }

        render( props, state, context ) {
            return h( Child, {
                ...props,
                [propName]: context && context[propName] || {},
            });
        }
    };
