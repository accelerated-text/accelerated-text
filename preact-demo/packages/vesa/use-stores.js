import { h, Component } from 'preact';
import * as R           from 'ramda';
import shallowEqual     from 'shallow-equal/objects';


export default storeNames => Child =>
    class VesaMultiStoreUser extends Component {

        state = R.zipObj(
            storeNames,
            storeNames.map( name => this.context.S[name].storeState ),
        );

        onStoreChange = R.zipObj(
            storeNames,
            storeNames.map( name => state => this.setState({ [name]: state })),
        );

        constructor( props, context ) {
            super( props, context );

            storeNames.forEach( name =>
                this.context.S[name].onChangeState(
                    this.onStoreChange[name]
                )
            );
        }

        componentWillUnmount() {

            storeNames.forEach( name =>
                this.context.S[name].offChangeState(
                    this.onStoreChange[name]
                )
            );
        }

        shouldComponentUpdate( nextProps, nextState, nextContext ) {

            return (
                !shallowEqual( nextProps, this.props )
                || !shallowEqual( nextState, this.state )
            );
        }

        render( props, state, context ) {
            return h( Child, {
                E:          this.context.E,
                ...state,
                ...props,
            });
        }
    };
