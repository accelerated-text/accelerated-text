import getValueConnections  from './get-value-connections';


export default block =>
    getValueConnections( block )
        .filter( connection => ! connection.isConnected());
