import { h, Component } from 'preact';

export default propName => Child =>
    class ContextConsumer extends Component {

        shouldComponentUpdate( nextProps, nextState, nextContext ) {

            return (
                nextProps !== this.props
                || nextContext !== this.context
            );
        }
        
        render( props, state, context ) {
            return (
                <Child { ...{
                    ...props,
                    [propName]: context && context[propName] || {},
                } }
                />
            );
        }
    };
