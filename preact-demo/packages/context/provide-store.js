import { h, Component }     from 'preact';
import { mapObjIndexed }    from 'ramda';


export default ( storeName, store ) => Child =>
    class ContextStoreProvider extends Component {

        state =     store.getInitialState( this.props, this );

        actions = mapObjIndexed(
            fn => arg => this.setState( fn( arg, this )),
            store
        );

        getChildContext() {
            return {
                [storeName]: {
                    ...this.actions,
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
