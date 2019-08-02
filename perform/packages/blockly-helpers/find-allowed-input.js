export default ( block, target ) => (
    target
    && ! target.allInputsFilled()
    && target.inputList.find( input => (
        input.connection
        && ! input.connection.isConnected()
        && input.connection.isConnectionAllowed( block.outputConnection )
    ))
);
