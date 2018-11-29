import { h, Component }     from 'preact';
import { mapObjIndexed }    from 'ramda';


export default ( propName, store ) => Child =>
    class ContextStoreProvider extends Component {

        state =     store.getInitialState( this.props, this );

        functions = mapObjIndexed(
            fn => arg => this.setState( fn( arg, this )),
            store
        );

        getChildContext() {
            return {
                [propName]: {
                    ...this.functions,
                    ...this.state,
                },
            };
        }

        render() {
            return h( Child, {
                ...this.props,
                ...this.getChildContext(),
            });
        }
    };
