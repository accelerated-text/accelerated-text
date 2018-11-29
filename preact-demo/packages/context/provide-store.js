import { h, Component } from 'preact';

const bindStoreFunctions = ( that, store ) => {
    const functions =   {};
    for( const k in store ) {
        functions[k] =  arg => that.setState( store[k]( arg, that ));
    }
    return functions;
};

export default ( propName, store ) => Child =>
    class ContextStoreProvider extends Component {

        state =     store.getInitialState( this.props, this );
        functions = bindStoreFunctions( this, store );

        getChildContext() {
            return {
                [propName]: {
                    ...this.functions,
                    ...this.state,
                },
            };
        }

        render() {
            const childProps = {
                ...this.props,
                ...this.getChildContext(),
            };
            return <Child { ...childProps } />;
        }
    };
