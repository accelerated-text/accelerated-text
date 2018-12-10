import { h, Component }     from 'preact';
import { mapObjIndexed }    from 'ramda';


export default ( storeName, store ) => Child =>
    class ContextStoreProvider extends Component {

        state =     store.getInitialState( this.props, this );

        events = mapObjIndexed(
            fn => arg => this.setState( fn( arg, this )),
            store
        );

        getChildContext() {
            return {
                [storeName]: {
                    ...this.events,
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
