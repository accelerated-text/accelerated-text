import getTarget                from './get-target';
import getStatementConnections  from './get-statement-connections';


export default block =>
    getStatementConnections( block )
        .map( getTarget )
        .filter( Boolean );
