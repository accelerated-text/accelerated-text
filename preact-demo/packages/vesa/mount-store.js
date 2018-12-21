import { h, Component }     from 'preact';
import { Dispatcher }       from 'flux';
import * as R               from 'ramda';
import shallowEqual         from 'shallow-equal/objects';

import ChangeDispatcher     from './change-dispatcher';
import createEvents         from './create-events';


export default ( storeName, store ) => Child => {

    const {
        componentDidMount,
        componentWillUnmount,
        getInitialState,
        ...eventHandlers
    } = store;

    return class VesaStoreComponent extends Component {

        /// Constructor --------------------------------------------------------

        dispatcher =        this.context.dispatcher || new Dispatcher;

        storeState = (
            getInitialState
                ? getInitialState( this.props )
                : null
        );

        changeDispatcher = (
            storeName
                ? new ChangeDispatcher
                : null
        );

        S = (
            storeName
                ? R.assoc( storeName, this, this.context.S )
                : this.context.S
        );

        E = createEvents( this.context.E, eventHandlers, this.dispatcher );

        onDispatch =    ({ arg, eventId, eventNamespace, eventName }) => {

            const getPath = R.path([ eventNamespace, eventName ]);
            const eventFn = getPath( this.E );

            /// console.log( storeName, 'onDispatch', eventNamespace, eventName, eventId, eventFn, typeof arg, this.E );
            if( !eventFn || eventId !== eventFn.eventId ) {
                /// Ignore events created in other Component tree branches.
                return;
            }

            const patch =   this.callEventHandler( getPath( eventHandlers ), arg );

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
                    this.changeDispatcher.dispatch( this.storeState );
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

        onChangeState =     storeName && this.changeDispatcher.register;
        offChangeState =    storeName && this.changeDispatcher.unregister;

        /// Register only when everything set-up:
        token =             this.dispatcher.register( this.onDispatch );

        /// Component lifecycle ------------------------------------------------

        getChildContext() {
            return {
                dispatcher: this.dispatcher,
                E:          this.E,
                S:          this.S,
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
            this.callEventHandler( componentWillUnmount, this.props );
        }

        render() {
            return h( Child, this.props );
        }
    };
};
