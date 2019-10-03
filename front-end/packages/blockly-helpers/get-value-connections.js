import getValueInputs       from './get-value-inputs';


export default block =>
    getValueInputs( block )
        .map( input => input.connection );
