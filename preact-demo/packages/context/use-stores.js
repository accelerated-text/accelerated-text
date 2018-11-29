import areShallowEqual  from 'shallow-equal/objects';
import { h, Component } from 'preact';
import { zipObj }       from 'ramda';

export default storeNames => Child =>
    class ContextStoreConsumer extends Component {

        shouldComponentUpdate( nextProps, nextState, nextContext ) {
            return (
                !areShallowEqual( nextProps, this.props )
                || storeNames.find(
                    name => !areShallowEqual( nextContext[name], this.context[name])
                )
            );
        }
        
        render( props, state, context ) {
            return h( Child, {
                ...props,
                ...zipObj(
                    storeNames,
                    storeNames.map( name => context && context[name] || {})
                ),
            });
        }
    };
