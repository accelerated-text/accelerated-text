import canConnectInputTargetToInput from './can-connect-input-target-to-input';
import getValueInputs               from './get-value-inputs';


export default ( sourceBlock, targetBlock ) => {

    getValueInputs( sourceBlock )
        /// use only connected inputs:
        .filter( oldInput => (
            oldInput.connection
            && oldInput.connection.targetConnection
        ))
        /// connect by name
        .filter( oldInput => {
            const newInput =    targetBlock.getInput( oldInput.name );
            if( canConnectInputTargetToInput( oldInput )( newInput )) {
                newInput.connection.connect(
                    oldInput.connection.targetConnection
                );
                return false;
            }
            return true;
        })
        /// connect to available inputs:
        .filter( oldInput => {
            const newInput =
                getValueInputs( targetBlock )
                    .find( canConnectInputTargetToInput( oldInput ));
            if( newInput ) {
                newInput.connection.connect(
                    oldInput.connection.targetConnection
                );
                return false;
            } else {
                return true;
            }
        })
        /// detach from old
        .forEach( oldInput => oldInput.connection.disconnect());
};
