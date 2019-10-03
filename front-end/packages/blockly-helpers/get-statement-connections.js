import getStatements        from './get-statements';


export default block =>
    getStatements( block )
        .map( input => input.connection );
