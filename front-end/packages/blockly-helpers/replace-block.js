import moveConnectionTarget from './move-connection-target';
import reconnectValues      from './reconnect-values';


export default ( sourceBlock, targetBlock ) => {

    /// move connected value blocks
    reconnectValues( sourceBlock, targetBlock );

    moveConnectionTarget(
        sourceBlock.outputConnection,
        targetBlock.outputConnection,
    );
    moveConnectionTarget(
        sourceBlock.nextConnection,
        targetBlock.nextConnection,
    );
    moveConnectionTarget(
        sourceBlock.previousConnection,
        targetBlock.previousConnection,
    );

    return targetBlock;
};
