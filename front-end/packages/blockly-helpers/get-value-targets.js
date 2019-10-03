import getTarget            from './get-target';
import getValueConnections  from './get-value-connections';


export default block =>
    getValueConnections( block )
        .map( getTarget )
        .filter( Boolean );
