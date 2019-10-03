import getStatementConnections  from './get-statement-connections';
import getThisAndNextBlocks     from './get-this-and-next-blocks';


export default block => [
    ( block.nextConnection
        && ! block.nextConnection.isConnected()
        && block.nextConnection /// return value
    ),
    ...getStatementConnections( block )
        .map( connection =>
             connection.isConnected()
                ? getThisAndNextBlocks( connection.targetBlock())
                    .pop()
                    .nextConnection
                : connection
        ),
].filter( Boolean );
