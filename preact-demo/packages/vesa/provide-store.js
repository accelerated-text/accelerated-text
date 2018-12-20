import { h, Component }     from 'preact';
import { Dispatcher }       from 'flux';
import * as R               from 'ramda';
import shallowEqual         from 'shallow-equal/objects';

import FnDispatcher         from './fn-dispatcher';
import addEventHandlers     from './add-event-handlers';


export default ( storeName, store ) => Child => {

    const {
        componentDidMount,
        componentWillUnmount,
        getInitialState,
        ...eventHandlers
    } = store;

    return class VesaStoreProvider extends Component {

        /// Constructor --------------------------------------------------------

        dispatcher =        this.context.dispatcher || new Dispatcher;

        storeState = (
            getInitialState
                ? getInitialState( this.props )
                : null
        );

        viewDispatcher = (
            storeName
                ? new FnDispatcher
                : null
        );

        S = (
            storeName
                ? R.assoc( storeName, this, this.context.S )
                : this.context.S
        );

        E = addEventHandlers( this.context.E, eventHandlers, this.dispatcher );

        onDispatch =    ({ arg, eventNamespace, eventName }) => {
            const patch = this.callEventHandler(
                R.path([ eventNamespace, eventName ], eventHandlers ),
                arg
            );

            const shouldUpdate = (
                patch
                && this.storeState
                && !R.whereEq( patch, this.storeState )
            );

            /// console.log( storeName, 'onDispatch', eventNamespace, eventName, arg, patch, shouldUpdate );

            if( shouldUpdate ) {
                this.storeState = R.mergeRight(
                    this.storeState,
                    patch,
                );
                if( storeName ) {
                    this.viewDispatcher.dispatch( this.storeState );
                }
            }
        };

        callEventHandler = ( fn, arg ) =>
            fn
                ? fn( arg, {
                    E:              this.E,
                    props:          this.props,
                    state:          this.storeState,
                    getStoreState:  this.getStoreState,
                })
                : null;

        getStoreState = name => {

            if( !( name in this.S )) {
                throw Error( `Store "${ name }" not found in context.` );
            }
            if( this.dispatcher.isDispatching()) {
                this.dispatcher.waitFor([ this.S[name].token ]);
            }
            return this.S[name].storeState;
        };

        /// Register only when everything set-up:
        token =         this.dispatcher.register( this.onDispatch );

        /// Component lifecycle ------------------------------------------------

        getChildContext() {
            return {
                dispatcher:     this.dispatcher,
                E:              this.E,
                S:              this.S,
            };
        }

        componentDidMount() {
            this.callEventHandler( componentDidMount, this.props );
        }

        shouldComponentUpdate( nextProps ) {
            return !shallowEqual( nextProps, this.props );
        }

        componentWillUnmount() {
            this.dispatcher.unregister( this.token );
            delete this.S[storeName];
            this.callEventHandler( componentWillUnmount, this.props );
        }

        render() {
            return h( Child, this.props );
        }
    };
};
