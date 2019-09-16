import { h }                from 'preact';


export default contexts => ChildComponent =>
    Object.entries( contexts )
        .reverse()
        .reduce(
            ( ChildComponent, [ name, Context ]) => (
                props =>
                    <Context.Consumer>{ value =>
                        <ChildComponent
                            { ...{ [name]: value } }
                            { ...props }
                        />
                    }</Context.Consumer>
            ),
            ChildComponent,
        );
