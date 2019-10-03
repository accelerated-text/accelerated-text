export default ( block, connection ) => (
    block
    && connection
    && (
        ( ! block.allInputsFilled()
            && block.inputList
                .map( input => input.connection )
                .filter( Boolean )
                .find( iconn => (
                    iconn
                    && ! iconn.isConnected()
                    && iconn.isConnectionAllowed( connection )
                ))
        ) || (
            block.nextConnection
            && ! block.nextConnection.isConnected()
            && block.nextConnection.isConnectionAllowed( connection )
            && block.nextConnection /// to be returned
        )
    )
);
